package controller;

import app.Main;

public abstract class AbstractController {

    protected Main main;

    public void setMainApp(Main main) {
        this.main = main;
    }
}