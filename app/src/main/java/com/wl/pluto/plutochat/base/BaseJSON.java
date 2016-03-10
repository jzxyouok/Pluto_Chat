package com.wl.pluto.plutochat.base;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 那我们这里的json在构造json数据的时候就是有要求的,如果是存放一个对象,那冒号前面的名称就必须是类名.
 * <p>
 * 如{"ZhangSan":{"name":"张三", "age": 25}},
 * 那我们在com.wl.mars.model里面就必须要有ZhangSan这个类
 * <p>
 * 如果json数据为{ "person": [ {"ZhangSan":{"name":"张三", "age":25}}
 * {"LiSi":{"name":"李四", "age":25}} {"WangWU":{"name":"王五", "age":25}} ] }
 * 那在我们的com.wl.mars.model里面就必须要有个Person类对应
 *
 * @author jeck
 */
public class BaseJSON {

    private String code;
    private String message;
    private String resultSrc;

    /**
     * 存储单个model对象的map
     */
    private Map<String, BaseModel> resultMap;

    /**
     * 存储整个json数据对应的(包含嵌套链表)map
     */
    private Map<String, ArrayList<? extends BaseModel>> resultList;

    public BaseJSON() {

        resultMap = new HashMap<String, BaseModel>();
        resultList = new HashMap<String, ArrayList<? extends BaseModel>>();
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getResultSrc() {
        return resultSrc;
    }

    public void setResultSrc(String resultSrc) {
        this.resultSrc = resultSrc;
    }

    /**
     * 通过类名获取对应的数据
     *
     * @param modelName
     * @return
     * @throws Exception
     */
    public BaseModel getResult(String modelName) throws Exception {

        BaseModel model = resultMap.get(modelName);

        if (model == null) {

            throw new Exception("Not found Model by name " + modelName);
        }

        return model;
    }

    /**
     * 根据对象名称获取相对应的数据链表(比如人这个对象链表, 里面有张三.李四,王五.赵六)
     *
     * @param modelName
     * @return
     * @throws Exception
     */
    public ArrayList<? extends BaseModel> getResultList(String modelName)
            throws Exception {

        //
        ArrayList<? extends BaseModel> modelList = this.resultList
                .get(modelName);

        // catch null exception
        if (modelList == null || modelList.size() == 0) {
            throw new Exception("Message data list is empty");
        }
        return modelList;
    }

    /**
     * 将json数据解析处理,并存储到一个map中(既可以是一个model对象,也可以是一个model对象链表)
     *
     * @param result
     * @throws Exception
     */
    public void setResult(String result) throws Exception {

        if (result.length() > 0) {

            // 先转化成json对象
            JSONObject jsonObject = new JSONObject(result);

            // 构造迭代器
            Iterator<String> iterator = jsonObject.keys();

            // 开始循环遍历这个json数据
            while (iterator.hasNext()) {

                // 获取一个名称
                String jsonKey = iterator.next();

                // 根据获取的名称,来获取一个model对象的名称,
                // 也就是说我们这个json数据中可以有多个model对象,只要是BaseModel类的子类就可以.
                String modelName = getModelName(jsonKey);

                // 然后就得到了这个类在我们整个工程中的完整路径,为反射做准备
                String modelClassName = "com.wl.mars.model." + modelName;

                // 看一下这个json数据是不是一个数组, 就是看一下jsonKey这个名称的":"后面是个什么东西
                JSONArray modelJsonArray = jsonObject.optJSONArray(jsonKey);

                // JSONObject, 如果是一个json对象
                if (modelJsonArray == null) {

                    // 转化成json对象
                    JSONObject modelJsonObject = jsonObject
                            .optJSONObject(jsonKey);

                    if (modelJsonObject == null) {
                        throw new Exception("message result is invalid");
                    }

                    // 解析这个json对象并把他放到我们的resultMap中
                    resultMap.put(modelName,
                            jsonToModel(modelClassName, modelJsonObject));

                    // JSONArray 如果是一个json数组
                } else {

                    // 如果是数组,那继续迭代,好像不行
                    // setResult(modelJsonArray.toString());

                    // 那就构造一个对应的链表
                    ArrayList<BaseModel> modelList = new ArrayList<BaseModel>();

                    // 开始遍历这个json数组
                    for (int i = 0; i < modelJsonArray.length(); i++) {

                        // 取一个json对象
                        JSONObject modelJsonObject = modelJsonArray
                                .optJSONObject(i);

                        // 构造迭代器
                        Iterator<String> it = modelJsonObject.keys();

                        if (it.hasNext()) {

                            // 获取一个名称
                            String key = it.next();

                            // 根据获取的名称,来获取一个model对象的名称,
                            // 也就是说我们这个json数据中可以有多个model对象,只要是BaseModel类的子类就可以.
                            String name = getModelName(key);

                            // 然后就得到了这个类在我们整个工程中的完整路径,为反射做准备
                            modelClassName = "com.wl.mars.model." + name;

                            String jsonStr = modelJsonObject.getString(key);

                            modelJsonObject = new JSONObject(jsonStr);
                        }

                        // 解析这个json对象并把他放入相对应的链表中, 现在的问题是modelClassName的名称不对
                        modelList.add(jsonToModel(modelClassName,
                                modelJsonObject));

                    }

                    // 最后把解析完的所有数据放到我们的resultList中
                    resultList.put(modelName, modelList);
                }
            }
        }
    }

    /**
     * 通过给定的名称来获取model对应的类名称
     *
     * @param str
     * @return
     */
    private String getModelName(String str) {

        // 这个地方又是一种神秘的协议,叫做正则表达式."\w"
        // 匹配包括下划线的任何单词字符。类似但不等价于“[A-Za-z0-9_]”，这里的"单词"字符使用Unicode字符集。
        // 说了这么多,其实就是按照"[],{},:"来提前字符串
        String[] strArray = str.split("\\w");

        // 如果找到
        if (strArray.length > 0) {

            // 把冒号前面的拿出来,冒号后面的在后面解析
            str = strArray[0];
        }

        // 把这个类名称的首字母转化成大写的
        //return AppUtils.upperCaseFirstLetter(str);
        return null;
    }

    /**
     * 将json数据转化成对应的model模型,这个地方就需要特别注意,我们自己定义的model数据(类)所对应的字段(field)
     * 一定要和json数据中所对应的model数据一一对应,这样才可以使用这个函数进行自动化解析(顺序可以不一样,但是名称必须一样)
     * <p>
     * 比如person类定义如下 class person{ private String name; private int age;
     * <p>
     * }
     * <p>
     * 那对应的json数据就应该是{"name":"zhangsan", "age":23}
     *
     * @param modelClassName  model类名
     * @param modelJsonObject modeljson对象
     * @return
     * @throws ClassNotFoundException
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws JSONException
     * @throws NoSuchFieldException
     */
    private BaseModel jsonToModel(String modelClassName,
                                  JSONObject modelJsonObject) throws ClassNotFoundException,
            InstantiationException, IllegalAccessException, JSONException,
            NoSuchFieldException {

        // auto load model class, 利用反射,来找到我们需要的类对象的 类名
        BaseModel modelObject = (BaseModel) Class.forName(modelClassName)
                .newInstance();

        Class<? extends BaseModel> modelClass = modelObject.getClass();

        // auto setting model field, 声明一个迭代器,我想这也是json对象做的比较好的地方.
        Iterator<String> iterator = modelJsonObject.keys();

        // 开始遍历这个json对象,将里面的{健:值}取出来放在我们的model对象中
        while (iterator.hasNext()) {

            // 获取一个字段名,对应json的名称
            String varField = iterator.next();

            // 获取这个字段名对应的值.对应json的值
            String varValue = modelJsonObject.getString(varField);

            // 通过反射获取model对象中对应的字段,(类里面的字段名)
            Field field = modelClass.getDeclaredField(varField);

            // 将这个字段设置为可访问的.因为避免private字段的限制
            field.setAccessible(true);

            // TODO 将我们之前获取的json数据设置到对应的model对象的字段中, 这个地方有问题,需要重新学习一下反射
            // age是int型的,你现在给的值是String类型的.所以会报IllegalArgumentExcellent,
            // 有tm的是类型不匹配
            field.set(modelObject, varValue);

        }

        // 返回我们填充好的model对象
        return modelObject;
    }

    @Override
    public String toString() {
        return "BaseMessage [code=" + code + ", message=" + message
                + ", resultSrc=" + resultSrc + "]";
    }

}
