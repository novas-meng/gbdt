import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by mengfanshan on 2017/7/9.
 */
public class GradientBoostedTreesModel {
    public double initvalue=0;
    public ArrayList<cartRegressionTree> treeModelArrayList=new ArrayList<>();
    public double learn_rate=0;
    String loss;
    public  GradientBoostedTreesModel(double learn_rate,String loss)
    {
        this.learn_rate=learn_rate;
        this.loss=loss;
    }
    public  GradientBoostedTreesModel()
    {
    }
    public  void addInitModel(double initvalue)
    {
        this.initvalue=initvalue;
    }
    public  void addCartTreeModel(cartRegressionTree tree)
    {
        treeModelArrayList.add(tree);
    }
    public series[] predict(series[] traindata)
    {
        System.out.println("预测"+initvalue);
        series[] predict_data=new series[traindata.length];
        for (int i=0;i<predict_data.length;i++)
        {
            series s=new series(initvalue,traindata[i].getX());
            predict_data[i]=s;
        }
        for (int i=0;i<treeModelArrayList.size();i++)
        {
            treeModelArrayList.get(i).predict(predict_data,learn_rate);
        }
        return predict_data;
    }
    //保存模型
    /*
    保存模型的方案如下
    第一行的顺序是initvalue,learn_rate,shuxingcount,treeModelArrayList.size() 使用逗号进行分割
    然后接下来treeModelArrayList.size()行，每一行保存每个树模型的节点信息
    具体方案为{flag,regression,split_index,split_value}#{flag,regression,split_index,split_value}#{flag,regression,split_index,split_value},...
     */
    public void saveModelToDisk(String filename)throws IOException
    {
        String lineSeparator = System.getProperty("line.separator", "\n");
        FileWriter fileWriter=new FileWriter(filename);
        StringBuilder stringBuilder=new StringBuilder();
        int tree_count=treeModelArrayList.size();
        System.out.println(tree_count);
        int shuxing_count=treeModelArrayList.get(0).getShuxingcount();
        stringBuilder.append(initvalue+","+learn_rate+","+shuxing_count+","+tree_count+","+loss+lineSeparator);
        for (int i=0;i<tree_count;i++)
        {
            cartRegressionTree cartTree=treeModelArrayList.get(i);
            ArrayList<TreeNode> treeNodeArrayList=cartTree.getModel();
            stringBuilder.append(treeNodeArrayList.get(0).saveToModelStr());
            for (int j=1;j<treeNodeArrayList.size();j++)
            {
                if(treeNodeArrayList.get(j)==null)
                {
                    stringBuilder.append("#null");
                }
                else
                {
                    stringBuilder.append("#"+treeNodeArrayList.get(j).saveToModelStr());
                }
            }
            stringBuilder.append(lineSeparator);
        }
        fileWriter.write(stringBuilder.toString());
        fileWriter.close();
    }
    public void loadModelFromDisk(String filename)throws IOException
    {
        BufferedReader bufferedReader=new BufferedReader(new FileReader(filename));
        String line=bufferedReader.readLine();
        System.out.println(line);
        String[] var=line.split(",");
        this.initvalue=Double.valueOf(var[0]);
        this.learn_rate=Double.valueOf(var[1]);
        int shuxingcount=Integer.valueOf(var[2]);
        int tree_count=Integer.valueOf(var[3]);

        for (int i=0;i<tree_count;i++)
        {
            line=bufferedReader.readLine();
            var=line.split("#");
            ArrayList<TreeNode> treeNodeArrayList=new ArrayList<>();
            for (int j=0;j<var.length;j++)
            {
                if(var[j].equals("null"))
                {
                    treeNodeArrayList.add(null);
                }
                else
                {
                    TreeNode treeNode=new TreeNode(var[j]);
                    treeNodeArrayList.add(treeNode);
                }
            }
            cartRegressionTree tree=new cartRegressionTree(shuxingcount,treeNodeArrayList);
            treeModelArrayList.add(tree);
        }
        bufferedReader.close();
    }
}
