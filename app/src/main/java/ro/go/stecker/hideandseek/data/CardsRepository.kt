package ro.go.stecker.hideandseek.data

import ro.go.stecker.hideandseek.R

val CardsRepository: List<CardDetails> = listOf(
//Curses
    CardDetails(
        id = 0,
        name = R.string.curse_bad_influencer,
        type = CardType.Curse,
        image = R.drawable.curse_of_the_bad_influencer,
        isPlayable = true,
        probability = 1
    ),
    CardDetails(
        id = 1,
        name = R.string.curse_bird_guide,
        type = CardType.Curse,
        image = R.drawable.curse_of_the_bird_guide,
        isPlayable = true,
        probability = 1
    ),
    CardDetails(
        id = 2,
        name = R.string.curse_distant_cuisine,
        type = CardType.Curse,
        image = R.drawable.curse_of_the_distant_cuisine,
        isPlayable = true,
        probability = 1
    ),
    CardDetails(
        id = 3,
        name = R.string.curse_drained_brain,
        type = CardType.Curse,
        image = R.drawable.curse_of_the_drained_brain,
        isPlayable = true,
        probability = 1
    ),
    CardDetails(
        id = 4,
        name = R.string.curse_egg_partner,
        type = CardType.Curse,
        image = R.drawable.curse_of_the_egg_partner,
        isPlayable = true,
        probability = 1
    ),
    CardDetails(
        id = 5,
        name = R.string.curse_endless_tumble,
        type = CardType.Curse,
        image = R.drawable.curse_of_the_endless_tumble,
        isPlayable = true,
        probability = 1
    ),
    CardDetails(
        id = 6,
        name = R.string.curse_evergrowing_economy,
        type = CardType.Curse,
        image = R.drawable.curse_of_the_evergrowing_economy,
        isPlayable = true,
        probability = 1
    ),
    CardDetails(
        id = 7,
        name = R.string.curse_gamblers_feet,
        type = CardType.Curse,
        image = R.drawable.curse_of_the_gamblers_feet,
        isPlayable = true,
        probability = 1
    ),
    CardDetails(
        id = 8,
        name = R.string.curse_hidden_hangman,
        type = CardType.Curse,
        image = R.drawable.curse_of_the_hidden_hangman,
        isPlayable = true,
        probability = 1
    ),
    CardDetails(
        id = 9,
        name = R.string.curse_impossible_riddle,
        type = CardType.Curse,
        image = R.drawable.curse_of_the_impossible_riddle,
        isPlayable = true,
        probability = 1
    ),
    CardDetails(
        id = 10,
        name = R.string.curse_impressionable_consumer,
        type = CardType.Curse,
        image = R.drawable.curse_of_the_impressionable_consumer,
        isPlayable = true,
        probability = 1
    ),
    CardDetails(
        id = 11,
        name = R.string.curse_impulsive_buyer,
        type = CardType.Curse,
        image = R.drawable.curse_of_the_impulsive_buyer,
        isPlayable = true,
        probability = 1
    ),
    CardDetails(
        id = 12,
        name = R.string.curse_jammed_door,
        type = CardType.Curse,
        image = R.drawable.curse_of_the_jammed_door,
        isPlayable = true,
        probability = 1
    ),
    CardDetails(
        id = 13,
        name = R.string.curse_luxury_car,
        type = CardType.Curse,
        image = R.drawable.curse_of_the_luxury_car,
        isPlayable = true,
        probability = 1
    ),
    CardDetails(
        id = 14,
        name = R.string.curse_mediocre_travel_agent,
        type = CardType.Curse,
        image = R.drawable.curse_of_the_mediocre_travel_agent,
        isPlayable = true,
        probability = 1
    ),
    CardDetails(
        id = 15,
        name = R.string.curse_music_guru,
        type = CardType.Curse,
        image = R.drawable.curse_of_the_music_guru,
        isPlayable = true,
        probability = 1
    ),
    CardDetails(
        id = 16,
        name = R.string.curse_neverending_forest,
        type = CardType.Curse,
        image = R.drawable.curse_of_the_neverending_forest,
        isPlayable = true,
        probability = 1
    ),
    CardDetails(
        id = 17,
        name = R.string.curse_overflowing_chalice,
        type = CardType.Curse,
        image = R.drawable.curse_of_the_overflowing_chalice,
        isPlayable = true,
        probability = 1
    ),
    CardDetails(
        id = 18,
        name = R.string.curse_right_turn,
        type = CardType.Curse,
        image = R.drawable.curse_of_the_right_turn,
        isPlayable = true,
        probability = 1
    ),
    CardDetails(
        id = 19,
        name = R.string.curse_spotty_memory,
        type = CardType.Curse,
        image = R.drawable.curse_of_the_spotty_memory,
        isPlayable = true,
        probability = 1
    ),
    CardDetails(
        id = 20,
        name = R.string.curse_u_turn,
        type = CardType.Curse,
        image = R.drawable.curse_of_the_u_turn,
        isPlayable = true,
        probability = 1
    ),
    CardDetails(
        id = 21,
        name = R.string.curse_unguided_tourist,
        type = CardType.Curse,
        image = R.drawable.curse_of_the_unguided_tourist,
        isPlayable = true,
        probability = 1
    ),
    CardDetails(
        id = 22,
        name = R.string.curse_urban_explorer,
        type = CardType.Curse,
        image = R.drawable.curse_of_the_urban_explorer,
        isPlayable = true,
        probability = 1
    ),
    CardDetails(
        id = 23,
        name = R.string.curse_voodoo_doll,
        type = CardType.Curse,
        image = R.drawable.curse_of_the_voodoo_doll,
        isPlayable = true,
        probability = 1
    ),
    CardDetails(
        id = 24,
        name = R.string.curse_zoologist,
        type = CardType.Curse,
        image = R.drawable.curse_of_the_zoologist,
        isPlayable = true,
        probability = 1
    ),


    CardDetails(
        id = 25,
        name = R.string.randomize_question,
        type = CardType.PowerUp,
        description = R.string.randomize_question_desc,
        image = R.drawable.randomize_question,
        probability = 4
    ),
    CardDetails(
        id = 26,
        name = R.string.veto_question,
        type = CardType.PowerUp,
        description = R.string.veto_question_desc,
        image = R.drawable.veto_question,
        probability = 4
    ),
    CardDetails(
        id = 27,
        name = R.string.duplicate_another_card,
        type = CardType.PowerUp,
        description = R.string.duplicate_card_desc,
        image = R.drawable.duplicate_another_card,
        isPlayable = true,
        probability = 2
    ),
    CardDetails(
        id = 28,
        name = R.string.move,
        type = CardType.PowerUp,
        description = R.string.move_desc,
        image = R.drawable.move,
        isPlayable = true,
        probability = 1
    ),
    CardDetails(
        id = 29,
        name = R.string.discard_1_draw_2,
        type = CardType.PowerUp,
        description = R.string.discard_1_draw_2_desc,
        image = R.drawable.discard_1_draw_2,
        isPlayable = true,
        probability = 4
    ),
    CardDetails(
        id = 30,
        name = R.string.discard_2_draw_3,
        type = CardType.PowerUp,
        description = R.string.discard_2_draw_3_desc,
        image = R.drawable.discard_2_draw_3,
        isPlayable = true,
        probability = 4
    ),


    CardDetails(
        id = 31,
        name = R.string.three_min_bonus,
        description = R.string.three_min_bonus_desc,
        type = CardType.TimeBonus,
        image = R.drawable.three_minute_bonus,
        probability = 25
    ),
    CardDetails(
        id = 32,
        name = R.string.five_min_bonus,
        type = CardType.TimeBonus,
        description = R.string.five_min_bonus_desc,
        image = R.drawable.five_minute_bonus,
        probability = 15
    ),
    CardDetails(
        id = 33,
        name = R.string.ten_min_bonus,
        type = CardType.TimeBonus,
        description = R.string.ten_min_bonus_desc,
        image = R.drawable.ten_minute_bonus,
        probability = 10
    ),
    CardDetails(
        id = 34,
        name = R.string.fifteen_min_bonus,
        type = CardType.TimeBonus,
        description = R.string.fifteen_min_bonus_desc,
        image = R.drawable.fifteen_minute_bonus,
        probability = 3
    ),
    CardDetails(
        id = 35,
        name = R.string.twenty_min_bonus,
        type = CardType.TimeBonus,
        description = R.string.twenty_min_bonus_desc,
        image = R.drawable.twenty_minute_bonus,
        probability = 2
    ),

)