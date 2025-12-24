package memory;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class SharedVectorTest {

    @Test
    public void testVectorAddition() {
        double[] vec1 = {1.0,2.0,3.0,4.0,5.0,6.0,7.0,8.0,9.0,10.0};
        SharedVector add1 = new SharedVector(vec1, VectorOrientation.ROW_MAJOR);
        
        //test 1: Successful addition
        
        //test 2: Non-matching dimensions

        //test 3: vector + null

        //test 4: Non-matching orientations
    }

    @Test
    public void testVectorVecMatMul() {
    }

    @Test
    public void testVectorTranspose() {
    }

    @Test
    public void testVectorNegate(){
    }
}