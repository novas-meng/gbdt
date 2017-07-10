import java.util.HashMap;

/**
 * Created by mengfanshan on 2017/7/8.
 */
public class TreeNode {
    //该节点下剩下的样本集合
    int[] left;
    //标记该节点是否是叶子节点
    int flag;
    //如果是叶子节点，该叶子节点表示的回归值
    double regression;
    //该节点的分类属性
    int split_index;
    //该节点的分类属性的划分值
    double split_value;
    //需要保存到模型中属性值个数
    int value_to_model_save=4;
    //该节点下minsum的值
    double minsum=0;
    //该节点位于的那一层的minsum的总和
    double layerminsum=0;
    //空构造函数
    public TreeNode()
    {

    }
    //从文件中，使用字符串生成树节点
    public TreeNode(String str)
    {
        loadFromModelStr(str);
    }
    @Override
    public String toString() {
        StringBuilder stringBuilder=new StringBuilder();
        stringBuilder.append("\t是否是叶子节点: "+flag+"\t");
        stringBuilder.append("\t该叶子节点包含样本数目: "+left.length+"\t");
        stringBuilder.append("\t分类属性索引: "+split_index+"\t");
        stringBuilder.append("\t分类属性值: "+split_value+"\t");
        stringBuilder.append("\t回归值: "+regression);
        stringBuilder.append("\t均方误差值: "+minsum);
        stringBuilder.append("\t该层误差值: "+layerminsum);

        return stringBuilder.toString();
    }
    public String saveToModelStr()
    {
        StringBuilder stringBuilder=new StringBuilder();
        stringBuilder.append("{"+flag+","+regression+","+split_index+","+split_value+"}");
        return stringBuilder.toString();
    }
    //输入的str格式必须是{} 这种格式
    public void loadFromModelStr(String str)
    {
        if(!str.startsWith("{")||!str.endsWith("}"))
        {
            System.out.println("树节点格式错误");
        }
        else
        {
            String s=str.substring(1,str.length()-1);
            String[] var=s.split(",");
            if(var.length!=value_to_model_save)
            {
                System.out.println("树节点格式错误");
            }
            else
            {
                try
                {
                    this.flag=Integer.valueOf(var[0]);
                    this.regression=Double.valueOf(var[1]);
                    this.split_index=Integer.valueOf(var[2]);
                    this.split_value=Double.valueOf(var[3]);
                }
                catch (Exception e)
                {
                    System.out.println("树节点格式错误");
                }
            }
        }
    }
}
