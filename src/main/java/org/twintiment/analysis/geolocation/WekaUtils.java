package org.twintiment.analysis.geolocation;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

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
	
	/**
	 * 
	 * @param insts
	 * @param size [0.01..1.0[
	 * @return
	 */
	public static Instances randomSample(Instances insts, double size) {
		if(size < 0.01 || size >= 1.0)
			return null;
		
		Instances sample = new Instances(insts, (int)(insts.numInstances()*size));
		// Add all TIDs to a list
		ArrayList<String> allTids = new ArrayList<>();
		for(int i=0; i<insts.numInstances(); ++i) {
			allTids.add(insts.instance(i).stringValue(insts.numAttributes()-1));
		}

		Random rnd = new Random();
		int i=0;
		while(sample.numInstances() < insts.numInstances()*size && i < sample.numInstances()) {
			if(	allTids.contains(insts.instance(i).stringValue(insts.numAttributes()-1))
				&& rnd.nextBoolean()) {

				//Remove the selected TID from the list
				allTids.remove(insts.instance(i).stringValue(insts.numAttributes()-1));
				//Each TID is associated with 404 instances
				for(int j=i; j<=i+404; ++j, ++i) {
					sample.add(insts.instance(j));
				}

			} else i+=404;

			//Start again if sample not full after iteration
			if(i == insts.numInstances()-1)
				i=0;
		}
		

		return sample;
	}
}
