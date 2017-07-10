/**
 * Created by mengfanshan on 2017/7/8.
 */
public class series {
    private double y=0;
    private double[] x;
    public series(double y,double[] x)
    {
        this.y=y;
        this.x=x;
    }
    public double getY()
    {
        return this.y;
    }
    public double[] getX()
    {
        return this.x;
    }
    public void setY(double y)
    {
        this.y=y;
    }
    public void setAndAddY(double y,double learn_rate)
    {
        this.y=this.y+learn_rate*y;
    }
    @Override
    public String toString() {
        StringBuilder stringBuilder=new StringBuilder();
        for (int i=0;i<x.length;i++)
        {
            stringBuilder.append(x[i]+",");
        }
        stringBuilder.append(y);
        return stringBuilder.toString();
    }
}
