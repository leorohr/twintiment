package org.twintiment.analysis.geolocation;

import java.io.File;
import java.io.IOException;

import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffSaver;

public class WekaUtils {
	public static void saveInstances(Instances instances, String toPath) throws IOException {
		ArffSaver saver = new ArffSaver();
		saver.setInstances(instances);
		saver.setFile(new File(toPath)); 
		saver.writeBatch();
	}
	
	public static Instance deepCopy(Instance inst) {
		Instance new_inst = new Instance(inst.numAttributes());
		double[] attrs = inst.toDoubleArray();
		for(int i=0; i<inst.numAttributes(); i++) {
			new_inst.setValue(i, attrs[i]);
		}
		
		return new_inst;
	}
	
	public static double attributeSum(Instance inst) {
		double sum = 0d;
		// Sum up attributes
		for(int i=0; i<inst.numAttributes(); i++)
			sum += inst.value(i);
		
		return sum;
	}
	
	public static void normaliseInstance(Instance inst) {
		double sum = attributeSum(inst);
		// Divide attributes by sum
		for(int i=0; i<inst.numAttributes(); i++)
			inst.setValue(i, inst.value(i)/sum);
	}

}
