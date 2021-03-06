package com.codedifferently.hurt;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class DataBuilder {

    static List<FoodItem> foodItemList = new ArrayList<>();
    static FoodContainers foodContainers = new FoodContainers();

    public static FoodItem buildClass(String dataStr) {
        List<String> properties = getPair(dataStr); // Creates a List of strings generated with our getPair method below...
        FoodItem foodItem = new FoodItem(properties.get(0), properties.get(1), properties.get(2), properties.get(3)); //uses our strings to create a new FoodItem from each one | Name, Price, Type, Expiration |
        foodItemList.add(foodItem); // add the objects to our getDataList above...
        return foodItem; //return for testing
    }

    public static List<String> getPair(String data) {
        List<String> properties = new ArrayList<>();

        String[] sections = data.split("([;:^%*@!])");
        String[] dataArr = removeOddStrings(sections);

        for (int i = 0; i < dataArr.length; i += 2) {
            try {
                properties.add(dataArr[i + 1]);
            } catch (IndexOutOfBoundsException e) {
                System.out.println("Had An Error!");
            }
        }
        return properties;
    }

    static List<FoodItem> getFoodItemsList() {
        return foodItemList;
    }

    public static String[] removeOddStrings(String[] strings) {
        String pattern = "c[o0][o0]kies";
        Pattern pat = Pattern.compile(pattern);

        for (int i = 0; i < strings.length; i++) {
            Matcher matcher = pat.matcher(strings[i]);
            if (strings[i].equals("")) strings[i] = "ERROR";
            if (matcher.find()) strings[i] = "cookies";
        }
        return strings;
    }

    public static void createLogFile() {
        File file = new File("finalOutput.txt");
        try {
            String output = getPrintedText();

            FileWriter writer = new FileWriter(file);
            writer.write(output);
            writer.flush();
            writer.close();

        } catch (IOException e) {
            System.out.println("Failed To Create Log File!");
        }
    }

    private static String getPrintedText() {
        StringBuilder output = new StringBuilder();
        foodContainers.fillContainers(getFoodItemsList());

        output.append(printOuter(foodContainers.getApples()));
        output.append(printOuter(foodContainers.getBread()));
        output.append(printOuter(foodContainers.getCookies()));
        output.append(printOuter(foodContainers.getMilk()));
        output.append(String.format("Errors            Seen:  %d Times\n", foodContainers.getErrors().size()));
        output.append("\n");

        return output.toString();
    }

    private static String printOuter(List<FoodItem> foodItems) {
        return String.format("Name:  %7s    Seen: %d  Times\n", foodItems.get(0).getName(), foodItems.size()) +
                "==============    ==============\n" +
                printInner(foodItems);
    }

    private static String printInner(List<FoodItem> foodItems) {
        StringBuilder innerOutput = new StringBuilder();
        Map<String, Long> map = getPriceCountByType(foodItems);
        for(Map.Entry<String, Long> price : map.entrySet()) {
            if(price.getKey().equals("ERROR")) continue;
            innerOutput.append(String.format("Price:  %6s    Seen: %d  Times\n", price.getKey(), price.getValue()));
            innerOutput.append("--------------    --------------\n");
        }
        innerOutput.append("\n");
        return innerOutput.toString();
    }


    public static Map<String, Long> getPriceCountByType(List<FoodItem> foodItemList) {
        return  foodItemList
                .stream()
                .map(FoodItem::getPrice)
                .collect(Collectors.groupingBy(
                        prices->prices,
                        Collectors.counting()
                ));
    }
}


