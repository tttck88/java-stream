
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

class StreamTest {

	/**
	 * 원본의 데이터를 변경 x
	 * 일회용
	 * 내부 반복으로 작업을 처리
	 */
	@Test
	 void stream1() {
		String[] nameArr = {"han", "jung", "taek"};
		List<String> nameList = Arrays.asList(nameArr);

		Stream<String> nameStream = nameList.stream();
		Stream<String> arrayStream = Arrays.stream(nameArr);

		// 반복문이 forEach라는 함수 내부에 숨겨져 있다.
		nameStream.sorted().forEach(System.out::println);
		arrayStream.sorted().forEach(System.out::println);
	}

	/**
	 * stream 생성
	 * 일회용이다...
	 */
	@Test
	void createStream() {
		// Collection의 스트림 생성
		// Collection 인터페이스에는 stream() 정의되어 있기에 구현한 객체들은 전부 생성 가능~~
		List<String> list = Arrays.asList("a","b","c");
		Stream<String> listStream = list.stream();
	}

	/**
	 * stream 가공
	 * 연산 결과를 Stream으로 다시 반환하기 때문에 연속해서 중간 연산을 이어갈 수 있다.
	 */
	@Test
	void processingStream() {
		// 필터링
		// 인자로 함수형 인터페이스 Predicate 받기에 boolean을 반환하는 람다식
		List<String> list = Arrays.asList("a","b","c");
		list.stream().filter(name -> name.contains("a")).peek(System.out::println);

		// 데이터 변환
		// 인자로 function을 받음
		list.stream().map(s -> s.toUpperCase()).peek(System.out::println);

		// 메소드 참조를 이용해 변경이 가능
		Stream<File> fileStream = Stream.of(new File("Test1.java"), new File("Test2.java"), new File("Test3.java"));
		fileStream.map(File::getName).peek(System.out::println);

		// 정렬
		// 인자로 Comparator을 받음
		List<String> languageList = Arrays.asList("Java", "Scala", "Groovy", "Python", "Go", "Swift");

		languageList.stream().sorted().peek(System.out::println);
		languageList.stream().sorted(Comparator.reverseOrder()).peek(System.out::println);

		// 중복제거
		// Object의 equals() 메소드를 사용
		// 생성한 클래스를 stream으로 사용한다면 equals와 hashCode를 오버라이드 해야함
		languageList.stream().distinct().peek(System.out::println);

		// 특정 연산 수행 작업을 수행할뿐 결과에는 영향 x
		// 인자로 Consumer를 받음
		int sum = IntStream.of(1, 3, 5, 7, 9)
			.peek(System.out::println)
			.sum();
		System.out.println("sum = " + sum);

		// 원시 Stream <-> Stream
		// mapToInt(), mapToLong(), mapToDouble()
		// mapToObject

		// Stream<Double> -> IntStream -> Stream<String>
		Stream.of(1.0, 2.0, 3.0).mapToInt(Double::intValue).mapToObj(i -> "a" + i);
		// IntStream -> Stream<Integer>
		IntStream.range(1, 4).mapToObj(i -> "a" + i);
	}

	/**
	 * stream 결과 만들기
	 * 생성된 stream을 바탕으로 결과를 위한 최종연산
	 */
	@Test
	void resultStream() {
		// 최댓값/최솟값/총합/평균/갯수 - Max/Min/Sum/Average/Count
		// min 이나 max 경우에는 비어있을 경우 값을 특정할수없기때문에 Optional로 반환된다.
		OptionalInt min = IntStream.of(1, 3, 5, 7, 9).min();
		int max = IntStream.of().max().orElse(0);
		IntStream.of(1, 3, 5, 7, 9).average().ifPresent(System.out::println);

		long count = IntStream.of(1, 3, 5, 7, 9).count();
		long sum = LongStream.of(1, 3, 5, 7, 9).sum();
		
		// 데이터 수집
		// stream의 요소들을 컬렉션으로 수집하고 싶을 경우 사용
		// Collector 타입을 인자로 받음..
		// collect() : 스트림 최종연산, 매개변수로 Collector를 받음
		// Collector : 인터페이스, collect의 파라미터는 이 인터페이스를 구현해야함
		// Collectors : 클래스, static 메소드로 미리 작성된 컬렉터를 제공한다.
		
		// collect의 파라미터로 Collector의 구현체가 와야함
		// Object collect(Collector collector)
		List<Product> productList = Arrays.asList(
			new Product(23, "potatoes"),
			new Product(14, "orange"),
			new Product(13, "lemon"),
			new Product(23, "bread"),
			new Product(13, "sugar")
		);

		List<String> nameList = productList.stream()
			.map(Product::getName)
			.collect(Collectors.toList());

		// String으로 이어붙여줌
		// 3개의 인자를 받음
		// delimiter : 중간에 들어가 구분시켜줌
		// prefix : 결과 맨 앞에 붙음
		// suffix : 결과 맨 뒤에 붙음
		String listToString1 = productList.stream()
			.map(Product::getName)
			.collect(Collectors.joining());

		String listToString2 = productList.stream()
			.map(Product::getName)
			.collect(Collectors.joining(" "));

		String listToString3 = productList.stream()
			.map(Product::getName)
			.collect(Collectors.joining(", ", "<", ">"));

		Double averageAmount = productList.stream()
			.collect(Collectors.averagingInt(Product::getAmount));

		Integer summingAmount = productList.stream().collect(Collectors.summingInt(Product::getAmount));

		Integer summingAmount2 = productList.stream().mapToInt(Product::getAmount).sum();

		// getCount(), getSum(), getAverage(), getMin(), getMax()
		IntSummaryStatistics statistics = productList.stream().collect(Collectors.summarizingInt(Product::getAmount));

		// 특정 그룹으로 묶기
		// 인자로 function을 받음
		// 결과는 map으로..
		Map<Integer, List<Product>> collectorMapOfLists = productList.stream()
			.collect(Collectors.groupingBy(Product::getAmount));
		/*
		{23=[Product{amount=23, name='potatoes'}, Product{amount=23, name='bread'}],
		 13=[Product{amount=13, name='lemon'}, Product{amount=13, name='sugar'}],
		 14=[Product{amount=14, name='orange'}]}
		 */
		
		// 특정 그룹으로 묶기...
		// 인자로 Predicate를 받고 boolean을 key값으로 분류함
		Map<Boolean, List<Product>> mapPartitioned = productList.stream().collect(Collectors.partitioningBy(p -> p.getAmount() > 15));
		/*
		{false=[Product{amount=14, name='orange'}, Product{amount=13, name='lemon'}, Product{amount=13, name='sugar'}],
		true=[Product{amount=23, name='potatoes'}, Product{amount=23, name='bread'}]}
		*/

		// 조건 검사
		// 인자로 Predicate를 받음
		// anyMatch 1개의 요소라도 해당 조건을 만족하는가
		// allMatch 모든 요소가 해당 조건을 만족하는가
		// nonMatch 모든 요소가 해당 조건을 만족하지 않는가
		List<String> names = Arrays.asList("Eric", "Elena", "Java");

		boolean anyMatch = names.stream()
			.anyMatch(name -> name.contains("a"));

		boolean allMatch = names.stream()
			.allMatch(name -> name.length() > 3);

		boolean noneMatch = names.stream()
			.noneMatch(name -> name.endsWith("s"));
	}

	static class Product {
		String name;
		int amount;

		Product(int amount ,String name) {
			this.amount = amount;
			this.name = name;
		}

		String getName() {
			return this.name;
		}

		int getAmount() {
			return this.amount;
		}
	}

}



















