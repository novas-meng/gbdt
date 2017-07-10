import org.jcp.xml.dsig.internal.dom.DOMUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * Created by mengfanshan on 2017/7/8.
 */
public class cartRegressionTree {
    //训练样本的维度
    private int shuxingcount;
    //存储着训练样本
    private series[] dataseries;
    //存储着树的所有节点，节点的存储方式，采用数组，因为cart 回归树是二叉完全树
    private ArrayList<TreeNode> treeNodeArrayList=new ArrayList<>();
    //存储着树的每一层的minsum
    private ArrayList<Double> minsumArrayList=new ArrayList<>();
    //构造训练集
    public cartRegressionTree(series[] dataseries)
    {
        if(dataseries.length<1)
        {
            System.out.println("训练数据集为空");
        }
        this.dataseries=dataseries;
        shuxingcount=this.dataseries[0].getX().length;
    }
    //从模型中读取cartRegressionTree树
    public cartRegressionTree(int shuxingcount,ArrayList<TreeNode> treeNodeArrayList)
    {
        this.shuxingcount=shuxingcount;
        this.treeNodeArrayList=treeNodeArrayList;
    }
    //获取训练集特征个数
    public int getShuxingcount()
    {
        return this.shuxingcount;
    }
    public ArrayList<TreeNode>  getModel()
    {
        return treeNodeArrayList;
    }
    //返回datas数组中不重复元素
    public double[] getDistinct(double[] datas)
    {
        HashMap<Double,Integer> map=new HashMap<>();
        for (int i=0;i<datas.length;i++)
        {
            map.put(datas[i],1);
        }
        double[] res=new double[map.size()];
        int index=0;
        for (Map.Entry<Double,Integer> entry:map.entrySet())
        {
            res[index++]=entry.getKey();
        }
        return res;
    }
    //获取某个属性的全部取值,dataseries 表示全样本，left中存储的是剩下的样本，index表示是第几个属性
    public double[] getshuxingAllX(series[] dataseries,int[] left,int index)
    {
        double[] res=new double[left.length];
        for (int i=0;i<left.length;i++)
        {
            series var=dataseries[left[i]];
            double[] datas= var.getX();
            res[i]=datas[index];
        }
        return res;
    }
    //获取剩下的样本的Y的值,dataseries 表示全样本，left中存储的是剩下的样本
    public double[] getshuxingAllY(series[] dataseries,int[] left)
    {
        double[] res=new double[left.length];
        for (int i=0;i<left.length;i++)
        {
            series var=dataseries[left[i]];
            res[i]=var.getY();
        }
        return res;
    }
    //返回值为数组，res[0]表示选择的是第几个属性，res[1]表示该属性的具体划分值
    public double[] getBestSplit(series[] dataseries,int[] left)
    {
        double minsum=Double.MAX_VALUE;
        double min_index=-1;
        double min_split=-1;
        int shuxingcount=dataseries[0].getX().length;
        System.out.println(shuxingcount);
        //key 为属性的索引，value 为该属性，剩下的样本中，所有的取值
        HashMap<Integer,double[]> indexmap=new HashMap<>();
        //该节点下。剩下的样本对应的Y
        double[] left_Y=getshuxingAllY(dataseries,left);
        for (int i=0;i<shuxingcount;i++)
        {
            indexmap.put(i,getshuxingAllX(dataseries,left,i));
        }
        for (Map.Entry<Integer,double[]> entry:indexmap.entrySet())
        {
           // System.out.println("===="+entry.getKey()+","+entry.getValue().length);
        }
        for (Map.Entry<Integer,double[]> entry:indexmap.entrySet())
        {
            int shuxing_index=entry.getKey();
            double[] shuxing_index_datas=entry.getValue();
            //System.out.println("shuxing_index_datas="+shuxing_index_datas.length);
            double[] shuxing_index_datas_distinct=QuickSort.sortASC(getDistinct(shuxing_index_datas));
            //System.out.println("shuxing_index_datas_distinct="+shuxing_index_datas_distinct.length);

            if(shuxing_index_datas_distinct.length<=2)
            {
                double[] res=new double[3];
                res[0]=-1;
                res[1]=-1;
                res[2]=0;
                return res;
            }
            //进行计算
            for (int i=1;i<shuxing_index_datas_distinct.length-1;i++)
            {
                double split=shuxing_index_datas_distinct[i];
                double c1_sum=0;
                double c2_sum=0;
                double c1_count=0;
                double c2_count=0;
                for (int j=0;j<shuxing_index_datas.length;j++)
                {
                    if(shuxing_index_datas[j]<split)
                    {
                        c1_count++;
                        c1_sum=c1_sum+left_Y[j];
                    }
                    else
                    {
                        c2_count++;
                        c2_sum=c2_sum+left_Y[j];
                    }
                }
                double c1=c1_sum/c1_count;
                double c2=c2_sum/c2_count;
                double sum=0;
                for (int j=0;j<shuxing_index_datas.length;j++)
                {
                    if(shuxing_index_datas[j]<split)
                    {
                        sum=sum+Math.pow(left_Y[j]-c1,2.0);
                    }
                    else
                    {
                        sum=sum+Math.pow(left_Y[j]-c2,2.0);
                    }
                }
                if(sum<minsum)
                {
                    min_index=shuxing_index;
                    min_split=split;
                    minsum=sum;
                }
            }

        }
        double[] res=new double[3];
        res[0]=min_index;
        res[1]=min_split;
        res[2]=minsum;
        return res;
    }
    //初始化left数组，初始的时候，left数组为0到n-1
    public static int[] initLeft(int n)
    {
        int[] left=new int[n];
        for (int i=0;i<left.length;i++)
        {
            left[i]=i;
        }
        return left;
    }
    public static int[][] getChildLeft(series[] dataseries,int[] left,int root_split_index,double root_split_value)
    {
        ArrayList<Integer> left_child_left_list=new ArrayList<>();
        ArrayList<Integer> right_child_left_list=new ArrayList<>();
        for (int i=0;i<left.length;i++)
        {
            series var=dataseries[left[i]];
            double value=var.getX()[root_split_index];
            if(value<root_split_value)
            {
                left_child_left_list.add(left[i]);
            }
            else
            {
                right_child_left_list.add(left[i]);
            }
        }
        int[] left_child_left_array=new int[left_child_left_list.size()];
        int[] right_child_left_array=new int[right_child_left_list.size()];
        for (int i=0;i<left_child_left_array.length;i++)
        {
            left_child_left_array[i]=left_child_left_list.get(i);
        }
        for (int i=0;i<right_child_left_array.length;i++)
        {
            right_child_left_array[i]=right_child_left_list.get(i);
        }
        int[][] res=new int[2][];
        res[0]=left_child_left_array;
        res[1]=right_child_left_array;
        return res;
    }
    //计算叶子节点的回归值
    public double getRegression(series[] dataseries,int[] left)
    {
        double sum=0;
        for (int i=0;i<left.length;i++)
        {
            sum=sum+dataseries[left[i]].getY();
        }
        return sum/left.length;
    }
    //找到叶子几点，计算叶子节点的回归值
    public void getLeafRegression(ArrayList<TreeNode> nodeArrayList,series[] dataseries,int layercount)
    {
        int leaf_begin_index=(int)Math.pow(2,layercount-1)-1;
        for (int i=leaf_begin_index;i<nodeArrayList.size();i++)
        {
            TreeNode leaf_node=nodeArrayList.get(i);
            if(leaf_node!=null)
            {
                leaf_node.flag=1;
                leaf_node.regression=getRegression(dataseries,leaf_node.left);
            }
        }
    }
    //计算每一层的minsum
    public double calLayerMinSum(ArrayList<TreeNode> treeNodeArrayList,int layer)
    {
        int beginindex=(int)Math.pow(2,layer-1)-1;
        double minsum=0;
        for (int i=beginindex;i<treeNodeArrayList.size();i++)
        {
            if(treeNodeArrayList.get(i)!=null)
            {
                minsum=minsum+treeNodeArrayList.get(i).minsum;
            }
        }
        for (int i=beginindex;i<treeNodeArrayList.size();i++)
        {
            if(treeNodeArrayList.get(i)!=null)
            {
                treeNodeArrayList.get(i).layerminsum=minsum;
            }
        }
        System.out.println("minsum="+minsum);
        return minsum;
    }
    public void build(int layercount)
    {
        int[] left=initLeft(dataseries.length);
        double[] res=getBestSplit(dataseries,left);
        int split_index=(int)res[0];
        double split_value=res[1];
        double minsum=res[2];
        System.out.println("minsum="+minsum);
        //System.out.println("split_index="+split_index);
        //System.out.println("split_value="+split_value);
        TreeNode rootNode=new TreeNode();
        rootNode.flag=0;
        rootNode.left=left;
        rootNode.regression=-1;
        rootNode.split_index=split_index;
        rootNode.split_value=split_value;
        rootNode.minsum=minsum;
        treeNodeArrayList.add(rootNode);
        int index=0;
        //当前计算的是第几层的minsum，从1开始
        int minsumlayerindex=1;
        while (treeNodeArrayList.size()<Math.pow(2,layercount)-1)
        {

            if(treeNodeArrayList.size()==Math.pow(2,minsumlayerindex)-1)
            {
                double layerminsum=calLayerMinSum(treeNodeArrayList,minsumlayerindex);
                minsumlayerindex++;
                minsumArrayList.add(layerminsum);
            }
            System.out.println(index+","+treeNodeArrayList.size());
            TreeNode node=treeNodeArrayList.get(index);
            index++;
            if(node==null)
            {
                treeNodeArrayList.add(null);
                treeNodeArrayList.add(null);
                continue;
            }

            //left_split_index=-1 说明该节点下剩下的样本数少于等于2个，不需要在扩展了
            if(node.split_index==-1)
            {
                //标记该节点为叶子节点
                node.flag=1;
                //计算该节点的回归值
                node.regression=getRegression(dataseries,left);
                //添加到数组中
                treeNodeArrayList.add(null);
                treeNodeArrayList.add(null);
            }
            else
            {
                int[] root_left=node.left;
                int root_split_index=node.split_index;
                double root_split_value=node.split_value;
                System.out.println("root_left="+root_left.length);

                System.out.println("root_split_index="+root_split_index);
                System.out.println("root_split_value="+root_split_value);
                int[][] root_res=getChildLeft(dataseries,root_left,root_split_index,root_split_value);

                //生成左子树节点
                int[] left_child_left=root_res[0];
                double[] left_res=getBestSplit(dataseries,left_child_left);
                int left_split_index=(int)left_res[0];
                double left_split_value=left_res[1];
                TreeNode left_child_node=new TreeNode();
                left_child_node.flag=0;
                left_child_node.left=left_child_left;
                left_child_node.regression=-1;
                left_child_node.split_index=left_split_index;
                left_child_node.split_value=left_split_value;
                left_child_node.minsum=left_res[2];
                //生成左子树节点
                int[] right_child_left=root_res[1];
                double[] right_res=getBestSplit(dataseries,right_child_left);
                int right_split_index=(int)right_res[0];
                double right_split_value=right_res[1];
                TreeNode right_child_node=new TreeNode();
                right_child_node.flag=0;
                right_child_node.left=right_child_left;
                right_child_node.regression=-1;
                right_child_node.split_index=right_split_index;
                right_child_node.split_value=right_split_value;
                right_child_node.minsum=right_res[2];

                //添加到数组中
                treeNodeArrayList.add(left_child_node);
                treeNodeArrayList.add(right_child_node);
            }
            System.out.println("size="+treeNodeArrayList.size());
            //当treeNodeArrayList的个数为1,3,7时，表示第一层，第二层，第三层数据已经生成结束，那么计算第一层，第二层，第三层的总的minsum，进行比较

        }

        double layerminsum=calLayerMinSum(treeNodeArrayList,minsumlayerindex);
        minsumlayerindex++;
        minsumArrayList.add(layerminsum);

        //在节点中，标记叶子节点，并且进行划分
        getLeafRegression(treeNodeArrayList,dataseries,layercount);
    }
    public static void toFile()throws IOException
    {
        FileWriter fileWriter=new FileWriter("data.csv");
        Random random=new Random();
        for (int i=0;i<10000;i++)
        {
            StringBuilder stringBuilder=new StringBuilder();
            int[] newint=new int[30];
            double sum=0;
            for (int j=0;j<newint.length;j++)
            {
                newint[j]=random.nextInt(1000);
                stringBuilder.append(newint[j]+",");
                sum=sum+newint[j];
            }
            stringBuilder.append(sum);
            fileWriter.write(stringBuilder.toString()+"\r\n");
        }
        fileWriter.close();
    }
    public static series[] getTrainData()throws IOException
    {
        BufferedReader bufferedReader=new BufferedReader(new FileReader("data.csv"));
        String line=bufferedReader.readLine();
        ArrayList<series> seriesArrayList=new ArrayList<>();
        while (line!=null)
        {
            String[] var=line.split(",");
            double[] x=new double[var.length-1];
            for (int i=0;i<var.length-1;i++)
            {
                x[i]=Double.valueOf(var[i]);
            }
            double y=Double.valueOf(var[var.length-1]);
            series s=new series(y,x);
            seriesArrayList.add(s);
            line=bufferedReader.readLine();
        }
        bufferedReader.close();
        series[] data=new series[seriesArrayList.size()];
        for (int i=0;i<data.length;i++)
        {
            data[i]=seriesArrayList.get(i);
        }
        return data;
    }
    public  void printTreeNode()
    {
        int sum=0;
        for (int i=0;i<treeNodeArrayList.size();i++)
        {
            TreeNode node=treeNodeArrayList.get(i);
            if(node==null)
            {
                System.out.println("null");
            }
            else
            {
                System.out.println(node);
                if(node.flag==1)
                {
                    sum=sum+node.left.length;
                }
            }
        }
        for (int i=0;i<minsumArrayList.size();i++)
        {
            System.out.println("第"+(i+1)+"层均值误差: "+minsumArrayList.get(i));
        }
        System.out.println("总的样本数为="+sum);
    }
    public  void predict(series[] dataseries)
    {
        if(dataseries.length<1)
        {
            System.out.println("预测数据集为空");
        }
        int predict_shuxing_count=dataseries[0].getX().length;
        if(predict_shuxing_count!=shuxingcount)
        {
            System.out.println("训练集属性个数为:"+shuxingcount+",然而测试集属性个数为:"+predict_shuxing_count);
            return;
        }
        for (int i=0;i<dataseries.length;i++)
        {
            series predict_series=dataseries[i];
            double[] x=predict_series.getX();
            TreeNode node=treeNodeArrayList.get(0);
            int index=0;
            while (node.flag!=1)
            {
                int split_index=node.split_index;
                double split_value=node.split_value;
                double predict_value=x[split_index];
                if(predict_value<split_value)
                {
                    index=2*index+1;
                }
                else
                {
                    index=2*index+2;
                }
                node=treeNodeArrayList.get(index);
            }
            double y=node.regression;
            predict_series.setY(y);
        }
    }
    //预测数据
    public  void predict(series[] dataseries,double learn_rate)
    {
        for (int i=0;i<dataseries.length;i++)
        {
            series predict_series=dataseries[i];
            double[] x=predict_series.getX();
            TreeNode node=treeNodeArrayList.get(0);
            int index=0;
            while (node.flag!=1)
            {
                int split_index=node.split_index;
                double split_value=node.split_value;
                double predict_value=x[split_index];
                if(predict_value<split_value)
                {
                    index=2*index+1;
                }
                else
                {
                    index=2*index+2;
                }
                node=treeNodeArrayList.get(index);
            }
            double y=node.regression;
            predict_series.setAndAddY(y,learn_rate);
        }
    }

    public static void main(String[] args)throws IOException
    {

        series[] traindata=getTrainData();
        int count=6;
        series[] newl=new series[count];
        for (int i=0;i<count;i++)
        {
            newl[i]=traindata[i];
        }
        //traindata=newl;
        System.out.println("样本个数："+traindata.length);
        cartRegressionTree tree=new cartRegressionTree(traindata);
        tree.build(13);
        tree.printTreeNode();
        tree.predict(newl);
    }
}
