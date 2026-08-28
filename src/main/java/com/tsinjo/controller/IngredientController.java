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
import com.tsinjo.mapper.ApiMapper;
import com.tsinjo.response.IngredientItemResponse;
import com.tsinjo.response.IngredientCategoryResponse;
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
    @Autowired private ApiMapper apiMapper;


    @PostMapping("category")
    public ResponseEntity<IngredientCategoryResponse> createIngredientCategory(
            @Valid @RequestBody IngredientCategoryRequest req,
            @RequestHeader("Authorization") String jwt) throws Exception {
        User user = userService.findUserByJwtToken(jwt);
        authorizationService.requireRestaurantOwnerOrAdmin(user, restaurantService.findRestaurantById(req.getRestaurantId()));
        IngredientCategory item=ingredientsService.createIngredientsCategory(req.getName(), req.getRestaurantId());
        return ResponseEntity.status(HttpStatus.CREATED).body(apiMapper.toIngredientCategoryResponse(item));
    }

    @PostMapping()
    public ResponseEntity<IngredientItemResponse> createIngredientItem(
            @Valid @RequestBody IngredientRequest req,
            @RequestHeader("Authorization") String jwt) throws Exception {
        User user = userService.findUserByJwtToken(jwt);
        authorizationService.requireRestaurantOwnerOrAdmin(user, restaurantService.findRestaurantById(req.getRestaurantId()));
        IngredientsItem item=ingredientsService.createIngredientItem(req.getRestaurantId(), req.getName(),req.getCategoryId());
        return ResponseEntity.status(HttpStatus.CREATED).body(apiMapper.toIngredientItemResponse(item));
    }

   @PutMapping("/{id}/stoke")
    public ResponseEntity<IngredientItemResponse> updateIngredientStock(
            @PathVariable Long id,
            @RequestHeader("Authorization") String jwt
   ) throws Exception {
        User user = userService.findUserByJwtToken(jwt);
        IngredientsItem existing = ingredientsService.findIngredientById(id);
        authorizationService.requireRestaurantOwnerOrAdmin(user, existing.getRestaurant());
        IngredientsItem item=ingredientsService.updateStock(id);
        return ResponseEntity.ok(apiMapper.toIngredientItemResponse(item));
    }

    @GetMapping("/restaurant/{id}")
    public ResponseEntity<List<IngredientItemResponse>> getRestaurantIngredient(
            @PathVariable Long id
    ) throws Exception {
        List<IngredientsItem> item=ingredientsService.findRestaurantIngredients(id);
        return ResponseEntity.ok(item.stream().map(apiMapper::toIngredientItemResponse).toList());
    }


    @GetMapping("/restaurant/{id}/category")
    public ResponseEntity<List<IngredientCategoryResponse>> getRestaurantIngredientCategory(
            @PathVariable Long id
    ) throws Exception {
        List<IngredientCategory> item=ingredientsService.findIngredientCategoryByRestaurantId(id);
        return ResponseEntity.ok(item.stream().map(apiMapper::toIngredientCategoryResponse).toList());
    }
}
