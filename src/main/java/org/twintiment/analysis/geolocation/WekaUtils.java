package org.twintiment.analysis.geolocation;

import java.io.File;
import java.io.IOException;

import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffSaver;

/**
 * A utility class to deal with WEKA instances. 
 */
public class WekaUtils {
	
	/**
	 * Store {@link Instances} to a file.
	 * @param instances The instances to store.
	 * @param toPath The path to create the file at.
	 * @throws IOException
	 */
	public static void saveInstances(Instances instances, String toPath) throws IOException {
		ArffSaver saver = new ArffSaver();
		saver.setInstances(instances);
		saver.setFile(new File(toPath)); 
		saver.writeBatch();
	}
	
	/**
	 * Create a deep copy of a WEKA {@link Instance}.
	 * @param inst The instance to copy.
	 * @return A deep copy of the instance.
	 */
	public static Instance deepCopy(Instance inst) {
		Instance new_inst = new Instance(inst.numAttributes());
		double[] attrs = inst.toDoubleArray();
		for(int i=0; i<inst.numAttributes(); i++) {
			new_inst.setValue(i, attrs[i]);
		}
		
		return new_inst;
	}
	
	/**
	 * Sums up all {@link Attribute} values of the {@code Instance}.
	 * @param inst The instance.
	 * @return The sum of the attributes.
	 */
	public static double attributeSum(Instance inst) {
		double sum = 0d;
		// Sum up attributes
		for(int i=0; i<inst.numAttributes(); i++)
			sum += inst.value(i);
		
		return sum;
	}
	
	/**
	 * Normalises all {@link Attribute}s in the {@link Instance}.
	 * Divides by the sum of all attribute values.
	 * @param inst
	 */
	public static void normaliseInstance(Instance inst) {
		double sum = attributeSum(inst);
		// Divide attributes by sum
		for(int i=0; i<inst.numAttributes(); i++)
			inst.setValue(i, inst.value(i)/sum);
	}

}
