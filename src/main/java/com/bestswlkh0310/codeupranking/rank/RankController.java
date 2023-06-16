package com.bestswlkh0310.codeupranking.rank;

import com.bestswlkh0310.codeupranking.util.CodeUpCrawler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

@Controller
@RequestMapping("/dgsw/code-up")
public class RankController {

    private final CodeUpCrawler codeUpCrawler;
    public RankController(CodeUpCrawler codeUpCrawler) {
        this.codeUpCrawler = codeUpCrawler;
    }

    @GetMapping("/rank")
    public String getRank(Model model) throws IOException, ParseException {
        List<Rank> rankList = codeUpCrawler.get();
        model.addAttribute("rankList", rankList);
        for (Rank rank: rankList) {
            System.out.println(rank.toString());
        }
        return "rank";
    }
}
