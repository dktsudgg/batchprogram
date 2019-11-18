package com.spring.yup.batchprogram.aboutconfig;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PropertyTest {

    @Autowired
    FruitProperty fruitProperty;

    @Test
    public void test(){
        List<Fruit> fruitData = fruitProperty.getList();
        assertThat(fruitData.get(0).getName(), is("banana"));
        assertThat(fruitData.get(0).getColor(), is("yellow"));

        assertThat(fruitData.get(1).getName(), is("apple"));
        assertThat(fruitData.get(1).getColor(), is("red"));

        assertThat(fruitData.get(2).getName(), is("water melon"));
        assertThat(fruitData.get(2).getColor(), is("green"));

        String testProperty = fruitProperty.getTestProperty();
        assertThat(testProperty, is("fruit test string"));

        String testProperty2 = fruitProperty.getTestProperty2();
        assertThat(testProperty2, is("aa"));

        String testProperty3 = fruitProperty.getTestProperty3();
        assertThat(testProperty3, is("bb"));

        String testProperty4 = fruitProperty.getTestProperty4();
        assertThat(testProperty4, is("cc"));
    }
}
