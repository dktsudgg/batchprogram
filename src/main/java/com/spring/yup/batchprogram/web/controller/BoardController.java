package com.spring.yup.batchprogram.web.controller;

import com.spring.yup.batchprogram.web.service.BoardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

// URL을 통해 넘어오는 통로 역할을 하며 서비스를 통해 처리된 데이터를 뷰 쪽에 바인딩시켜줌
// src/resources/templates를 기준으로 데이터를 바인딩할 타깃의 뷰 경로를 지정

@Controller
@RequestMapping("/board")
public class BoardController {

    @Autowired
    BoardService boardService;

    // @RequestParam 어노테이션을 사용하여 idx 파라미터를 필수로 받음.
    // 만약 바인딩할 값이 없으면 기본값 ‘0’으로 설정됨.
    // findBoardByIdx(idx)로 조회 시 idx 값을 ‘0’으로 조회하면 board 값은 null값으로 반환됨.
    @GetMapping({"", "/"})  // 매핑경로를 중괄호를 사용하여 여러개를 받을 수 있음.
    public String board(
            @RequestParam(value = "idx", defaultValue = "0") Long idx
            , Model model
    ){
        model.addAttribute("board", boardService.findById(idx));
        return "/board/form";
    }

    // @PageableDefault 어노테이션의 파라미터인 size, sort, direction 등을 사용하여 페이징 처리에 대한 규약을 정의할 수 있음.
    @GetMapping("/list")
    public String list(@PageableDefault Pageable pageable, Model model){
        model.addAttribute("boardList", boardService.findBoardList(pageable));
        return "/board/list";
    }

}
