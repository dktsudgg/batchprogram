package com.spring.yup.batchprogram;

import com.spring.yup.batchprogram.web.domain.Board;
import com.spring.yup.batchprogram.web.domain.User;
import com.spring.yup.batchprogram.web.domain.enums.BoardType;
import com.spring.yup.batchprogram.web.repository.BoardRepository;
import com.spring.yup.batchprogram.web.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.stream.IntStream;

@RestController
@SpringBootApplication
public class BatchprogramApplication {

	public static void main(String[] args) {
		SpringApplication.run(BatchprogramApplication.class, args);
	}

	@GetMapping("/hello")
	public String hello(){
		return "hello kyoujin's batch program";
	}

	// 애플리케이션 구동 후 CommandLineRunner로 테스트용 데이터를 DB에 넣는 작업..
	// CommandLineRunner는 애플리케이션 구동 후 특정 코드를 실행시키고 싶을 때 직접 구현하는 인터페이스..
	// 애플리케이션 구동 시 테스트 데이터를 함께 생성하여 데모 프로젝트를 실행/테스트하고 싶을 때 편리함..
	// 또한 여러 CommandLineRunner를 구현하여 같은 애플리케이션 컨텍스트의 빈에 사용할 수 있음..

	// 한 명의 회원을 생성하여 그 회원이 글 200개를 작성하는 쿼리를 생성
	@Bean
	public CommandLineRunner runner(UserRepository userRepository, BoardRepository boardRepository) throws Exception {
		// 자바 8 람다 표현식을 사용..
		return (args) -> {
			User user = userRepository.save(User.builder()
					.name("havi")
					.password("test")
					.email("havi@gmail.com")
					.createdDate(LocalDateTime.now())
					.build()
			);

			IntStream.rangeClosed(1, 200).forEach(
					index -> boardRepository.save(Board.builder()
							.title("게시글"+index)
							.subTitle("순서"+index)
							.content("콘텐츠")
							.boardType(BoardType.free)
							.createdDate(LocalDateTime.now())
							.updatedDate(LocalDateTime.now())
							.user(user)
							.build())
			);
		};
	}
}
