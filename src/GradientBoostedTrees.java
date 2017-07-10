import java.io.IOException;

/**
 * Created by mengfanshan on 2017/7/9.
 */
public class GradientBoostedTrees {
    //n_tree表示树的个数,learn_rate表示学习率
    int n_tree;
    double learn_rate;
    int layercount;
    String loss;
    GradientBoostedTreesModel model;
    public GradientBoostedTrees()
    {

    }
    public GradientBoostedTrees(int n_tree,double learn_rate,int layercount,String loss)
    {
        this.n_tree=n_tree;
        this.learn_rate=learn_rate;
        this.layercount=layercount;
        this.loss=loss;
    }
    public  double getInitPredictValue(series[] traindata)
    {
        double init_sum=0;
        for (int i=0;i<traindata.length;i++)
        {
            init_sum=init_sum+traindata[i].getY();
        }
        return init_sum/traindata.length;
    }
    public series[] getNegGradientSeries(series[] true_data,series[] predict_data)
    {
        //此时负梯度为 yi-F(xi)
        if(loss.equals("ls"))
        {
            for (int i=0;i<true_data.length;i++)
            {
                predict_data[i].setY(true_data[i].getY()-predict_data[i].getY());
            }
        }
        return predict_data;
    }
    public GradientBoostedTreesModel fit(series[] traindata)
    {
        GradientBoostedTreesModel model=new GradientBoostedTreesModel(learn_rate,loss);
        double init_y=getInitPredictValue(traindata);
        model.addInitModel(init_y);
        for (int i=0;i<n_tree;i++)
        {
            System.out.println("第"+i+"棵树在构建");
            series[] predict=model.predict(traindata);
            series[] negGradientSeries=getNegGradientSeries(traindata,predict);
            cartRegressionTree treeModel=new cartRegressionTree(negGradientSeries);
            treeModel.build(layercount);
            model.addCartTreeModel(treeModel);
        }
        this.model=model;
        return model;
    }
    //进行预测
    public series[] predict(series[] predict_series)
    {
        return model.predict(predict_series);
    }
    //保存模型到硬盘
    public void saveModelToDisk(String filename)throws IOException
    {
        model.saveModelToDisk(filename);
    }
    //从硬盘中读取模型
    public void loadModelFromDisk(String filename)throws IOException
    {
        this.model=new GradientBoostedTreesModel();
        this.model.loadModelFromDisk(filename);
    }
    public static void main(String[] args)throws IOException
    {
        long begin=System.currentTimeMillis();
        series[] traindata=cartRegressionTree.getTrainData();
        int count=6;
        series[] newl=new series[count];
        for (int i=0;i<count;i++)
        {
            newl[i]=traindata[i];
        }


        System.out.println("样本个数："+traindata.length);
        GradientBoostedTrees gbdt=new GradientBoostedTrees(1000,0.1,5,"ls");
        gbdt.fit(traindata);

        long end=System.currentTimeMillis();
        System.out.println("时间："+(end-begin));
        gbdt.saveModelToDisk("gbdt.model");


/*
        GradientBoostedTrees gbdt=new GradientBoostedTrees();
        gbdt.loadModelFromDisk("gbdt.model");
        newl=gbdt.predict(newl);
        for (int i=0;i<newl.length;i++)
        {
            System.out.println(newl[i]);
        }
*/
    }
}
