package ru.javawebinar.topjava.util;

import ru.javawebinar.topjava.model.UserMeal;
import ru.javawebinar.topjava.model.UserMealWithExcess;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.*;
import java.util.stream.Collectors;

import static ru.javawebinar.topjava.util.TimeUtil.isBetweenHalfOpen;

public class UserMealsUtil {
    public static void main(String[] args) {
        List<UserMeal> meals = Arrays.asList(
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 10, 0), "Завтрак", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 13, 0), "Обед", 1000),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 20, 0), "Ужин", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 0, 0), "Еда на граничное значение", 100),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 10, 0), "Завтрак", 1000),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 13, 0), "Обед", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 20, 0), "Ужин", 410)
        );

        List<UserMealWithExcess> mealsTo = filteredByCycles(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000);
        mealsTo.forEach(System.out::println);

        System.out.println(filteredByStreams(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000));
    }

    public static List<UserMealWithExcess> filteredByCycles(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        // return filtered list with excess. Implement by cycles
        List<UserMealWithExcess> res = new ArrayList<>();
        List<UserMeal> temp = new ArrayList<>();
        Map<LocalDate, Integer> caloriesSumPerDay = new HashMap<>();

        // если данные поступают из ненадёжного источника, надо выполнить валидацию аргументов

        for (UserMeal meal : meals) {
            LocalDate date = meal.getDateTime().toLocalDate();
            LocalTime time = meal.getDateTime().toLocalTime();

            if (caloriesSumPerDay.containsKey(date)) {
                caloriesSumPerDay.put(date, caloriesSumPerDay.get(date) + meal.getCalories());
            } else {
                caloriesSumPerDay.put(date, meal.getCalories());
            }

            if (isBetweenHalfOpen(time, startTime, endTime)) {
                temp.add(meal);
            }
        }

        for (UserMeal meal : temp) {
            LocalDate date = meal.getDateTime().toLocalDate();

            if (caloriesSumPerDay.get(date) <= caloriesPerDay) {
                res.add(new UserMealWithExcess(
                        meal.getDateTime(),
                        meal.getDescription(),
                        meal.getCalories(),
                        false
                ));
            }
        }

        return res;
    }

    public static List<UserMealWithExcess> filteredByStreams
            (List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        // Implement by streams

        Map<LocalDate, Integer> caloriesSumPerDay = new HashMap<>();

        meals.forEach(meal -> {
            LocalDate date = meal.getDateTime().toLocalDate();

            if (caloriesSumPerDay.containsKey(date)) {
                caloriesSumPerDay.put(date, caloriesSumPerDay.get(date) + meal.getCalories());
            } else {
                caloriesSumPerDay.put(date, meal.getCalories());
            }
        });

        List<UserMealWithExcess> res = meals.stream()
                .filter(meal ->
                        isBetweenHalfOpen(meal.getDateTime().toLocalTime(), startTime, endTime)
                                && caloriesSumPerDay.get(meal.getDateTime().toLocalDate()) <= caloriesPerDay
                )
                .map(meal -> new UserMealWithExcess(meal.getDateTime(), meal.getDescription(), meal.getCalories(), false))
                .collect(Collectors.toList());

        return res;
    }
}
