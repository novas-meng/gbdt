/**
 * Created by mengfanshan on 2017/7/8.
 */
public class QuickSort {
    public static void main(String[] args)
    {
        double[] array={-1};
        double[] res=sortASC(array);
        for (int i=0;i<res.length;i++)
        {
            System.out.println(res[i]);
        }

    }
    public static int partition(double[] array,int p,int q)
    {
        int index=p-1;
        double key=array[q];
        double temp=0;
        for (int i=p;i<q;i++)
        {
            if(array[i]<key)
            {
                index++;
                temp=array[i];
                array[i]=array[index];
                array[index]=temp;
            }
        }
        index++;
        temp=array[index];
        array[index]=array[q];
        array[q]=temp;
        return index;
    }
    public static void quicksort(double[] array,int p,int q)
    {
        if(p<q)
        {
            int partition=partition(array,p,q);
            quicksort(array,p,partition-1);
            quicksort(array,partition+1,q);
        }
    }
    public static double[] sortASC(double[] res)
    {
        double[] temp=new double[res.length];
        System.arraycopy(res,0,temp,0,res.length);
        quicksort(temp,0,temp.length-1);
        return temp;
    }
}
