package com.atguigu.myssm.io;

import com.atguigu.myssm.util.StringUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.servlet.ServletContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @author adventure
 * @create 2022-05-21 13:12
 */
public class ClassPathXMLApplicationContext implements BeanFactory{

     /*
    读取"applicationContext.xml"文件，
    将获取到的：
        id="fruit" 转化为 String的类型，
        class="com.atguigu.fruit.controllers.FruitController" 转化为 Object的类型
    存入到beanMap中
     */

    //专门用来存储各种Controller。
    // 通过配置文件读取到的"地址值"，存到beanMap中，通过前端发来的数据来进行指定调用
    //例如FruitConntroller
    private Map<String,Object> beanMap = new HashMap<>();
    private String path = "applicationContext.xml";

    public ClassPathXMLApplicationContext(){
        this("applicationContext.xml");
    }
    public ClassPathXMLApplicationContext(String path){
        if (StringUtil.isEmpty(path)){
            throw new RuntimeException("IOC容器的配置文件没有指定。。。");
        }
        try {
            //创建一个IO流，读取"applicationContext.xml"文件
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream(path);

            //1.创建DocumentBuilderFactory
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();

            //2.创建DocumentBuilder对象
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();


            //3.创建Document对象，目的是，调用getElementsByTagName()方法，
            //  来获取"applicationContext.xml"中的标签，以节点(Node)的形式存储
            Document doucment = documentBuilder.parse(inputStream);

            //4.获取所有的bean节点，以(类似于)集合的形式存储
            NodeList beanNodeList = doucment.getElementsByTagName("bean");
            //遍历，得到每一个bean节点，并存入beanMap中
            for (int i = 0; i < beanNodeList.getLength(); i++) {
                Node beanNode = beanNodeList.item(i);

                //判断获取的bean节点是否是元素节点
                if (beanNode.getNodeType()==Node.ELEMENT_NODE){

                    //如果是元素节点，则强转为Element
                    //用来获取“application.xml”中<bean>标签中的id,class属性的值
                    Element beanElement = (Element) beanNode;
                    String beanId = beanElement.getAttribute("id");
                    String className = beanElement.getAttribute("class");

                    /*
                    Class.forName():
                    A a = (A)Class.forName(“pacage.A”).newInstance();
                    这和你 A a = new A()； 是一样的效果。
                     */
                    Class beanClass = Class.forName(className);

                    //创建bean实例
                    Object beanObj = beanClass.newInstance();
                    //将bean实例对象保存到Map容器中
                    beanMap.put(beanId,beanObj);
                    //到目前为止，此处需要注意的是：bean和bean之间的依赖关系还没有设置

                }

            }

            //5.组装bean之间的依赖关系
            for (int i = 0; i < beanNodeList.getLength(); i++) {

                Node beanNode = beanNodeList.item(i);

                //判断获取的bean节点是否是元素节点
                if (beanNode.getNodeType()==Node.ELEMENT_NODE) {

                    //如果是元素节点，则强转为Element
                    //用来获取“application.xml”中<bean>标签中的id,class属性的值
                    Element beanElement = (Element) beanNode;
                    String beanId = beanElement.getAttribute("id");

                    //获取<bean>标签中的子节点,
                    // 有三个，两个空白文本节点和<property>
                    NodeList beanChildNodesList = beanElement.getChildNodes();
                    for(int j = 0;j < beanChildNodesList.getLength();j++){
                        Node beanChildNode = beanChildNodesList.item(j);
                        if (beanNode.getNodeType()==Node.ELEMENT_NODE && "property".equals(beanChildNode.getNodeName())){
                            Element propertyElement = (Element) beanChildNode;

                            String propertyName = propertyElement.getAttribute("name");
                            String propertRef = propertyElement.getAttribute("ref");

                            //(1)找到propertyRef对应的实例
                            Object refObj = beanMap.get(propertRef);

                            //(2)将refObj设置到当前bean对应的实例的property属性上去
                            Object beanObj = beanMap.get(beanId);

                            //(3)将refObj，设置到beanObj的proper属性上面，从而实现依赖。用反射实现
                            Class beanClazz = beanObj.getClass();
                            Field propertyField = beanClazz.getDeclaredField(propertyName);

                            propertyField.setAccessible(true);
                            propertyField.set(beanObj,refObj);

                        }

                    }


                }
            }

        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Object getBean(String id) {
        return beanMap.get(id);
    }
}
