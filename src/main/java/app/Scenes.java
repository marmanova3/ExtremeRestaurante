package app;

public enum Scenes {
    CASH_REGISTER_WINDOW("/windows/cashRegisterWindow.fxml"),
    CHOOSE_ITEMS_WINDOW("/windows/chooseItemsWindow.fxml"),
    MAIN_WINDOW("/windows/mainWindow.fxml"),
    POP_UP_WINDOW("/windows/popUpWindow.fxml"),
    TABLE_WINDOW("/windows/tableWindow.fxml");

    private final String fxmlPath;

    Scenes(final String fxmlPath) {
        this.fxmlPath = fxmlPath;
    }

    @Override
    public String toString() {
        return fxmlPath;
    }
}
