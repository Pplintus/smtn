import models.Environment;

public class MainConsoleMany {

    public static void main(String[] args) throws InterruptedException {

        for (int i = 1; i <= 20; i++){
            System.out.println("Попытка симуляции " + i );
            Environment world = new Environment(30, 300, 80, 22);
            world.showMore(100000);
        }
    }

}
