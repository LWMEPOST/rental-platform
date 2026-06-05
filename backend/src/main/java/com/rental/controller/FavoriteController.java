package com.rental.controller;

import com.rental.common.result.Result;
import com.rental.entity.Device;
import com.rental.service.UserFavoriteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/favorite")
@Tag(name = "Favorite", description = "User Favorite API")
public class FavoriteController {

    @Autowired
    private UserFavoriteService userFavoriteService;

    @GetMapping("/check")
    @Operation(summary = "Check if device is favorited")
    public Result<Boolean> checkFavorite(@RequestParam Long userId, @RequestParam Long deviceId) {
        return Result.success(userFavoriteService.isFavorite(userId, deviceId));
    }

    @PostMapping("/toggle")
    @Operation(summary = "Toggle favorite status")
    public Result<Void> toggleFavorite(@RequestBody Map<String, Long> params) {
        Long userId = params.get("userId");
        Long deviceId = params.get("deviceId");
        if (userId == null || deviceId == null) {
            return Result.error(400, "参数错误");
        }
        userFavoriteService.toggleFavorite(userId, deviceId);
        return Result.success(null);
    }

    @GetMapping("/list/{userId}")
    @Operation(summary = "Get user favorite list")
    public Result<List<Device>> getFavoriteList(@PathVariable Long userId) {
        return Result.success(userFavoriteService.getUserFavorites(userId));
    }
}
