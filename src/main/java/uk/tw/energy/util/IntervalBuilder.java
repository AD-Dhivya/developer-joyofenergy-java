package uk.tw.energy.util;

import uk.tw.energy.domain.ElectricityReading;
import uk.tw.energy.domain.Interval;

import java.util.ArrayList;
import java.util.List;

public class IntervalBuilder {
    public List<Interval> buildIntervalList(List<ElectricityReading> readingList){
        List<Interval>intervalList = new ArrayList<>();
        for(int i=0;i<readingList.size()-1;i++){
            intervalList.add(new Interval(readingList.get(i),readingList.get(i+1)));
        }
        return intervalList;

    }
}