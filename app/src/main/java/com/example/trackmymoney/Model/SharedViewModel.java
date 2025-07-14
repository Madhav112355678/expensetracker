package com.example.trackmymoney.Model;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import org.jspecify.annotations.Nullable;

public class SharedViewModel extends ViewModel {

    private final MutableLiveData<Long> decrement = new MutableLiveData<>();
    private final MutableLiveData<Integer> isthismonth = new MutableLiveData<>();
    public void setDecrement(long value) {
        decrement.setValue(value);
    }
    public MutableLiveData<Long> getDecrement() {
        return decrement;
    }

    public void setthismonth(int monthvalue) {
        isthismonth.setValue(Integer.valueOf(monthvalue));
    }

    public MutableLiveData<Integer> getthismonth() {
        return isthismonth ;
    }

}