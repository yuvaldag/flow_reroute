package flow_generator;

import java.util.Random;

public interface Distribution {
	double getRandomSample(Random randomGen);
}
