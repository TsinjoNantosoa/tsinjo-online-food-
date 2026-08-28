package com.tsinjo.controller;

import com.tsinjo.model.IngredientCategory;
import com.tsinjo.model.IngredientsItem;
import com.tsinjo.request.IngredientCategoryRequest;
import com.tsinjo.request.IngredientRequest;
import com.tsinjo.service.IngredientsService;
import com.tsinjo.service.UserService;
import com.tsinjo.service.RestaurantService;
import com.tsinjo.service.AuthorizationService;
import com.tsinjo.model.User;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/ingredients")
public class IngredientController {

    @Autowired
    private IngredientsService ingredientsService;
    @Autowired private UserService userService;
    @Autowired private RestaurantService restaurantService;
    @Autowired private AuthorizationService authorizationService;


    @PostMapping("category")
    public ResponseEntity<IngredientCategory> createIngredientCategory(
            @Valid @RequestBody IngredientCategoryRequest req,
            @RequestHeader("Authorization") String jwt) throws Exception {
        User user = userService.findUserByJwtToken(jwt);
        authorizationService.requireRestaurantOwnerOrAdmin(user, restaurantService.findRestaurantById(req.getRestaurantId()));
        IngredientCategory item=ingredientsService.createIngredientsCategory(req.getName(), req.getRestaurantId());
        return  new ResponseEntity<>(item, HttpStatus.CREATED);
    }

    @PostMapping()
    public ResponseEntity<IngredientsItem> createIngredientItem(
            @Valid @RequestBody IngredientRequest req,
            @RequestHeader("Authorization") String jwt) throws Exception {
        User user = userService.findUserByJwtToken(jwt);
        authorizationService.requireRestaurantOwnerOrAdmin(user, restaurantService.findRestaurantById(req.getRestaurantId()));
        IngredientsItem item=ingredientsService.createIngredientItem(req.getRestaurantId(), req.getName(),req.getCategoryId());
        return  new ResponseEntity<>(item, HttpStatus.CREATED);
    }

   @PutMapping("/{id}/stoke")
    public ResponseEntity<IngredientsItem> updateIngredientStock(
            @PathVariable Long id,
            @RequestHeader("Authorization") String jwt
   ) throws Exception {
        User user = userService.findUserByJwtToken(jwt);
        IngredientsItem existing = ingredientsService.findIngredientById(id);
        authorizationService.requireRestaurantOwnerOrAdmin(user, existing.getRestaurant());
        IngredientsItem item=ingredientsService.updateStock(id);
        return  new ResponseEntity<>(item, HttpStatus.OK);
    }

    @GetMapping("/restaurant/{id}")
    public ResponseEntity<List<IngredientsItem>> getRestaurantIngredient(
            @PathVariable Long id
    ) throws Exception {
        List<IngredientsItem> item=ingredientsService.findRestaurantIngredients(id);
        return  new ResponseEntity<>(item, HttpStatus.OK);
    }


    @GetMapping("/restaurant/{id}/category")
    public ResponseEntity<List<IngredientCategory>> getRestaurantIngredientCategory(
            @PathVariable Long id
    ) throws Exception {
        List<IngredientCategory> item=ingredientsService.findIngredientCategoryByRestaurantId(id);
        return  new ResponseEntity<>(item, HttpStatus.OK);
    }
}
