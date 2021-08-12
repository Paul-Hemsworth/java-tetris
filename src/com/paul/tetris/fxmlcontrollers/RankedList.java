package com.paul.tetris.fxmlcontrollers;

import javafx.collections.ObservableList;
import javafx.collections.ObservableListBase;
import java.util.Collection;
import java.util.Comparator;

public class RankedList<T extends ScoresController.ScoreEntry> extends ObservableListBase<T>{
    private final ObservableList<T> list;
    private final Comparator<T> comparator;

    public RankedList(ObservableList<T> list, Comparator<T> comparator){
        rankList(list, comparator);
        this.list = list;
        this.comparator = comparator;
    }

    @Override
    public boolean add(T entry){
        // Special case for adding first element
        if (list.size() == 0){
            entry.rankProperty().set(1);
            list.add(entry);
            return true;
        }

        // Use loop to converge on insertion index
        int idx, lo = 0, hi = list.size();

        while (lo != hi){
            idx = (hi + lo) / 2;

            int compVal = comparator.compare(entry, list.get(idx));

            if (compVal < 0){
                hi = idx;
            } else if (compVal > 0){
                lo = idx + 1;
            } else {
                System.out.println("Entry already in list or comparator result is invalid");
                System.out.printf("Rank = %d\tName = %s\tScore = %d\n", entry.rankProperty().get(), entry.nameProperty().get(), entry.scoreProperty().get());
                System.out.printf("Rank = %d\tName = %s\tScore = %d\n", list.get(idx).rankProperty().get(), list.get(idx).nameProperty().get(), list.get(idx).scoreProperty().get());
                return false;
            }
        }

        // hi and lo have converged on insertion index
        idx = lo;

        // Set rankProperty of entry
        if (idx == 0){
            entry.rankProperty().set(1);
        } else if (idx <= list.size()){
            T lastEntry = list.get(idx - 1);
            int lastRank = lastEntry.rankProperty().get();

            if (entry.scoreProperty().get() == lastEntry.scoreProperty().get()){
                entry.rankProperty().set(lastRank);
            } else {
                entry.rankProperty().set(lastRank + 1);
            }
        }

        // Add entry at index
        list.add(idx, entry);

        // Adjust all rankProperties of elements following idx
        idx++;
        while (idx < list.size()){
            T lastEntry = list.get(idx - 1);
            T thisEntry = list.get(idx);

            if (thisEntry.scoreProperty().get() == lastEntry.scoreProperty().get()){
                thisEntry.rankProperty().set(lastEntry.rankProperty().get());
            } else {
                thisEntry.rankProperty().set(lastEntry.rankProperty().get() + 1);
            }
            idx++;
        }

        return true;
    }


    @Override
    public boolean setAll(Collection<? extends T> elements){
        boolean ret = list.setAll(elements);
        rankList();
        return ret;
    }

    @Override
    public T get(int i) {
        return list.get(i);
    }

    @Override
    public int size() {
        return list.size();
    }

    public void clear(){
        list.clear();
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        for (ScoresController.ScoreEntry entry: list){
            sb.append(entry.rankProperty().get());
            sb.append(entry.nameProperty().get());
            sb.append(entry.scoreProperty().get());
            sb.append("\n");
        }
        return sb.toString();
    }

    private void rankList(){
        rankList(list, comparator);
    }

    private void rankList(ObservableList<T> list, Comparator<T> comparator){
        if (list.size() == 0){
            return;
        }

        list.sort(comparator);

        int rank = 1;
        list.get(0).rankProperty().set(rank);

        for (int idx = 1; idx < list.size(); idx++){
            T lastEntry = list.get(idx - 1), currentEntry = list.get(idx);
            if (currentEntry.scoreProperty().get() != lastEntry.scoreProperty().get()){
                rank++;
            }

            currentEntry.rankProperty().set(rank);
        }
    }
}