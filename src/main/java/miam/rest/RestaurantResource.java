package miam.rest;

import com.google.common.base.Optional;
import miam.domain.Meal;
import miam.domain.Restaurant;
import org.bson.types.ObjectId;
import restx.Status;
import restx.annotations.*;
import restx.factory.Component;
import restx.jongo.JongoCollection;
import restx.security.PermitAll;

import javax.inject.Named;

import static restx.common.MorePreconditions.checkEquals;

@Component
@RestxResource
@PermitAll
public class RestaurantResource {
    private final JongoCollection restaurants;

    public RestaurantResource(@Named("restaurants") JongoCollection restaurants) {
        this.restaurants = restaurants;
    }

    @GET("/restaurants")
    public Iterable<Restaurant> findrestaurants(Optional<String> name) {
        if (name.isPresent()) {
            return restaurants.get().find("{name: #}", name.get()).as(Restaurant.class);
        } else {
            return restaurants.get().find().as(Restaurant.class);
        }
    }

    @POST("/restaurants")
    public Restaurant createRestaurant(Restaurant restaurant) {
        restaurants.get().save(restaurant);
        return restaurant;
    }

    @GET("/restaurants/{oid}")
    public Optional<Restaurant> findRestaurantById(String oid) {
        return Optional.fromNullable(restaurants.get().findOne(new ObjectId(oid)).as(Restaurant.class));
    }

    @PUT("/restaurants/{oid}")
    public Restaurant updateRestaurant(String oid, Restaurant restaurant) {
        checkEquals("oid", oid, "restaurant.key", restaurant.getKey());
        restaurants.get().save(restaurant);
        return restaurant;
    }

    @DELETE("/restaurants/{oid}")
    public Status deleteRestaurant(String oid) {
        restaurants.get().remove(new ObjectId(oid));
        return Status.of("deleted");
    }

    @GET("/restaurants/{oid}/meals")
    public Iterable<Meal> getMeals(String oid) {
        Optional<Restaurant> restaurantById = this.findRestaurantById(oid);
        if (restaurantById.isPresent()) {
            Restaurant restaurant = restaurantById.get();
            return restaurant.getMeals();
        }
        return null;
    }

    @POST("/restaurants/{oid}/meals")
    public Meal createMeal(String oid, Meal meal) {
        Optional<Restaurant> restaurantOptional = Optional.fromNullable(restaurants.get().findOne(new ObjectId(oid)).as(Restaurant.class));

        if (restaurantOptional.isPresent()) {
            Restaurant restaurant = restaurantOptional.get();
            restaurant.getMeals().add(meal);
            restaurants.get().save(restaurantOptional.get());

            return meal;
        }
        return null;
    }

    @GET("/restaurants/{oid}/meals/{mid}")
    public Iterable<Meal> getMeals(String oid, String mid) {
        Optional<Restaurant> restaurantById = this.findRestaurantById(oid);
        if (restaurantById.isPresent()) {
            Restaurant restaurant = restaurantById.get();
            return restaurant.getMeals();
        }
        return null;
    }
}
