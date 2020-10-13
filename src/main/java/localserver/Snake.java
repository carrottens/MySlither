package localserver;

import de.mat2095.my_slither.SnakeBodyPart;

import java.util.Deque;

class Snake {

    int id;
    String name;
    int x;
    int y;
    int wang;
    int ang;
    int sp;
    int fam;
    int tsp;
    Deque<SnakeBodyPart> bodyParts;

    Snake(int id, String name, int x, int y, int wang, int ang, int sp, int fam, int tsp, Deque<SnakeBodyPart> sbp) {
        this.id = id;
        this.name = name;
        this.x = x;
        this.y = y;
        this.wang = wang;
        this.ang = ang;
        this.sp = sp;
        this.fam = fam;
        this.tsp = tsp;
        this.bodyParts = sbp;
    }
}
