package com.bosco.stdata.teaModel;

import lombok.Data;

@Data
public class Telpas2024 {
     private String adminDate;  // 1-4                      ALT

     //  0325 = Spring 2025 = 2024-2025                 ALT and Reg
     //  0324 = Spring 2024 = 2023-2024                 ALT and REG
     
     private String studentId;  // 145-153                  ALT     2023    2023ALT

     private String gradeLevel;  // 5-6                     ALT     2023    2023ALT

    private String compositeRating;   // 908-908                    5       ALT     2023    2023ALT
                        // 0 = No Rating Available
                        // 1 = Beginning
                        // 2 = Intermediate
                        // 3 = Advanced
                        // 4 = Advanced high 


                        // This is for ALT!!!
                        // 0 = Score code is not S
                        // 1 = Awareness
                        // 2 = Imitation
                        // 3 = Early Independence
                        // 4 = Developing Independence
                        // 5 = Basic Fluency

    private String listeningScore;     // 309-312                  1  ALT   2023    2023ALT       
	private String speakingScore;      // 507-510                  3  ALT   2023    2023ALT
	private String readingScore;       //709-712                   4  ALT   2023    2023ALT
	private String writingScore;       // 437-440                  2 2023   *** NOT IN ALT   This is 759-762 for ALT    2023ALT same



     private String throwaway; // 1199  
}
