package com.mpc.file.pgmwriter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.mpc.sampler.NoteParameters;
import com.mpc.sampler.Program;
import com.mpc.sampler.Sampler;

public class SampleNames {

	private final char[] sampleNamesArray;

	private final int numberOfSamples;

	private List<Integer> snConvTable;
	
	public SampleNames(Program program, Sampler sampler) {

		snConvTable = new ArrayList<Integer>();
		List<Integer> list = new ArrayList<Integer>();
		
		for (NoteParameters nn : program.getNotesParameters())
			if (nn.getSndNumber() != -1)
				list.add(new Integer(nn.getSndNumber()));
	
		Collections.sort(list);

		Set<String> sampleNamesSet = new HashSet<String>();
		List<String> finalNames = new ArrayList<String>();
		
		
		for (int i = 0; i < list.size(); i++)
			
			if (sampleNamesSet.add(sampler
					.getSoundName(list.get(i).intValue())))
				finalNames.add(sampler.getSoundName(list.get(i).intValue()));


		for (int i=0;i<sampler.getSoundCount();i++) {
			
			int j = -1;
			
			for (int k = 0;k<finalNames.size();k++)
				if (finalNames.get(k).equals(sampler.getSoundName(i))) {
					j = k;
					break;
				}
			
			snConvTable.add(new Integer(j));
		}
		

		numberOfSamples = finalNames.size();
		sampleNamesArray = new char[(numberOfSamples * 17) + 2];

		int counter = 0;

		for (String s : finalNames)
			setSampleName(counter++, s);

		sampleNamesArray[sampleNamesArray.length - 2] = 0x1E;
		sampleNamesArray[sampleNamesArray.length - 1] = 0x00;
	}

	char[] getSampleNamesArray() {
		return sampleNamesArray;
	}

	private void setSampleName(int sampleNumber, String name) {

		char[] nameArray = name.toCharArray();

		for (int i = 0; i < nameArray.length; i++)
			sampleNamesArray[i + (sampleNumber * 17)] = nameArray[i];

		for (int i = nameArray.length; i < 16; i++)
			sampleNamesArray[i + (sampleNumber * 17)] = 0x20;

		sampleNamesArray[16 + (sampleNumber * 17)] = 0x00;
	}

	int getNumberOfSamples() {
		return numberOfSamples;
	}
	
	List<Integer> getSnConvTable() {
		return snConvTable;
	}
}