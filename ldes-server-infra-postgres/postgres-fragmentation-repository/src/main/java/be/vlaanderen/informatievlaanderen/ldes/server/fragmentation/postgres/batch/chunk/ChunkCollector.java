package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.postgres.batch.chunk;

import com.google.common.collect.Streams;
import org.springframework.batch.item.Chunk;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

public class ChunkCollector<T> implements Collector<T, Chunk<T>, Chunk<T>> {
	@Override
	public Supplier<Chunk<T>> supplier() {
		return Chunk<T>::new;
	}

	@Override
	public BiConsumer<Chunk<T>, T> accumulator() {
		return Chunk::add;
	}

	@Override
	public BinaryOperator<Chunk<T>> combiner() {
		return (left, right) -> new Chunk<>(Streams.concat(left.getItems().stream(), right.getItems().stream()).toList());
	}

	@Override
	public Function<Chunk<T>, Chunk<T>> finisher() {
		return Function.identity();
	}

	@Override
	public Set<Characteristics> characteristics() {
		return Collections.unmodifiableSet(EnumSet.of(Collector.Characteristics.UNORDERED, Collector.Characteristics.IDENTITY_FINISH));
	}
}
