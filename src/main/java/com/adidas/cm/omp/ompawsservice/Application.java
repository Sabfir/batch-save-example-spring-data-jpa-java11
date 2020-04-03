package com.adidas.cm.omp.ompawsservice;

import static java.util.stream.Collectors.toList;
import static java.util.stream.IntStream.range;

import com.integralblue.log4jdbc.spring.Log4jdbcAutoConfiguration;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@EnableJpaRepositories(considerNestedRepositories = true)
@SpringBootApplication(exclude = {Log4jdbcAutoConfiguration.class})
//@EntityScan(basePackages = {"com.adidas.cm.omp.ompawsservice",
//	"com.adidas.cm.omp.aws.service.entities"})
@AllArgsConstructor
public class Application {
	private final ModelRepo modelRepo;
	private final ArticleRepository article_Repository_2;

	public interface ModelRepo extends JpaRepository<Model, UUID> {}
	public interface ArticleRepository extends JpaRepository<Article, Long>{}


	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@EventListener
	public void onReady(ApplicationReadyEvent e) {
		modelRepo.saveAll(range(0, 3).mapToObj(Model::new).collect(toList()));
		saveArticle(1, 26);
		saveArticle(26, 51);
		saveArticle(51, 76);

		modelRepo.saveAll(range(0, 3).mapToObj(Model::new).collect(toList()));
		saveArticle(1, 26);
		saveArticle(26, 51);
		saveArticle(51, 76);
	}

	@Entity
	@NoArgsConstructor
	@Data
	static class Model {
		@Id
		@GenericGenerator(
			name = "modelSeqGen",
			strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
			parameters = {
				@Parameter(name = "sequence_name", value = "hibernate_sequence"),
				@Parameter(name = "optimizer", value = "pooled"),
				@Parameter(name = "initial_value", value = "1"),
				@Parameter(name = "increment_size", value = "100")
			}
		)
		@GeneratedValue(
			strategy = GenerationType.SEQUENCE,
			generator = "modelSeqGen"
		)
		private Long id;
		private Integer number;

		Model(Integer number) {
			this.number = number;
		}
	}

	@Entity
	@NoArgsConstructor
	@Data
	static class Article {
		@Id
		@GenericGenerator(
			name = "articleSeqGen",
			strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
			parameters = {
				@Parameter(name = "sequence_name", value = "hibernate_sequence"),
				@Parameter(name = "optimizer", value = "pooled"),
				@Parameter(name = "initial_value", value = "1"),
				@Parameter(name = "increment_size", value = "100")
			}
		)
		@GeneratedValue(
			strategy = GenerationType.SEQUENCE,
			generator = "articleSeqGen"
		)
		private Long artId;
		@Column
		private String artNumber;
	}

	@RestController
	@AllArgsConstructor
	class TestController {
		private ArticleRepository articleRepository;

		@GetMapping({"/test"})
		public void save() {
			saveArticle(0, 25);
		}

		@GetMapping({"/testOne"})
		public void saveOne() {
			Article article = new Article();
			article.setArtNumber(RandomStringUtils.random(5, true, false));
			articleRepository.save(article);
		}
	}

	@Transactional
	public void saveArticle(int from, int to) {
		article_Repository_2.saveAll(range(from, to).mapToObj((int i) -> {
			Article article_2 = new Article();
			article_2.setArtNumber("artNumber_" + i);
			return article_2;
		}).collect(toList()));
	}
}
