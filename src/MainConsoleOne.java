import models.Environment;

public class MainConsoleOne {

    public static void main(String[] args) throws InterruptedException {

        Environment world = new Environment(30, 300, 80, 12);
        world.showOne(1000);

    }
}
