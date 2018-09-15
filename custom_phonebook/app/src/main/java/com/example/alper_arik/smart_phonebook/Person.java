package com.example.alper_arik.smart_phonebook;

import java.io.Serializable;

public class Person implements Serializable{
    private String _id;
    private String _name;
    private String _homePhoneNumber;
    private String _mobilePhoneNumber;
    private String _workPhoneNumber;
    private String _eMail;


    public Person(){

    }

    public Person(String _id, String _name, String _homePhoneNumber, String _mobilePhoneNumber, String _workPhoneNumber, String _eMail) {
        this._id = _id;
        this._name = _name;
        this._homePhoneNumber = _homePhoneNumber;
        this._mobilePhoneNumber = _mobilePhoneNumber;
        this._workPhoneNumber = _workPhoneNumber;
        this._eMail = _eMail;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String get_name() {
        return _name;
    }

    public void set_name(String _name) {
        this._name = _name;
    }

    public String get_homePhoneNumber() {
        return _homePhoneNumber;
    }

    public void set_homePhoneNumber(String _homePhoneNumber) {
        this._homePhoneNumber = _homePhoneNumber;
    }

    public String get_mobilePhoneNumber() {
        return _mobilePhoneNumber;
    }

    public void set_mobilePhoneNumber(String _mobilePhoneNumber) {
        this._mobilePhoneNumber = _mobilePhoneNumber;
    }

    public String get_workPhoneNumber() {
        return _workPhoneNumber;
    }

    public void set_workPhoneNumber(String _workPhoneNumber) {
        this._workPhoneNumber = _workPhoneNumber;
    }

    public String get_eMail() {
        return _eMail;
    }

    public void set_eMail(String _eMail) {
        this._eMail = _eMail;
    }
}
