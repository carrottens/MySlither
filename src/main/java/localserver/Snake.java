package localserver;

import de.mat2095.my_slither.SnakeBodyPart;

import java.util.Deque;
import java.util.LinkedList;

class Snake {

    int id;
    String name;
    double x;
    double y;
    double wang;
    double ang;
    double sp;
    double fam;
    double tsp;
    double directionAng;
    double directionX;
    double directionY;
    Deque<SnakeBodyPart> bodyParts;

    Snake(int id, String name, double x, double y, double wang, double ang, double sp, double fam, double tsp) {
        this.id = id;
        this.name = name;
        this.x = x;
        this.y = y;
        this.wang = wang;
        this.ang = ang;
        this.sp = sp;
        this.fam = fam;
        this.tsp = tsp;
        this.bodyParts = new LinkedList<>();

        for(int i = 0; i < 20; i += 2) {
            bodyParts.add(new SnakeBodyPart(i + 1, i + 2));
        }
    }
}
