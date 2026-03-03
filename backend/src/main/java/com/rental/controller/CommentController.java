package com.rental.controller;

import com.rental.common.result.Result;
import com.rental.entity.RentalComment;
import com.rental.service.RentalCommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comment")
public class CommentController {

    @Autowired
    private RentalCommentService commentService;

    @GetMapping("/list/{deviceId}")
    public Result<List<RentalComment>> getList(@PathVariable Long deviceId) {
        return Result.success(commentService.getByDeviceId(deviceId));
    }

    @PostMapping("/add")
    public Result<String> add(@RequestBody RentalComment comment) {
        try {
            commentService.addComment(comment);
            return Result.success("评价成功");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
