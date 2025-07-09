package com.example.trackmymoney.Model;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SharedViewModel extends ViewModel {

    private final MutableLiveData<Long> decrement = new MutableLiveData<>();

    public void setDecrement(long value) {
        decrement.setValue(value);
    }
    public MutableLiveData<Long> getDecrement() {
        return decrement;
    }
}