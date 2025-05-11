import model.Battlefield;
import model.Tank;
import repository.DBWorker;
import view.MainWindow;

public class Main {
    public static void main(String[] args) {
        // При старте загружаем данные из БД
        Battlefield battlefield = DBWorker.loadBattlefield();
        new MainWindow(battlefield);
        for (Tank tank : battlefield.getTanks()) {
            System.out.println(tank.getName() + " (HP: " + tank.getHPTank() + ")");
        }
    }
}