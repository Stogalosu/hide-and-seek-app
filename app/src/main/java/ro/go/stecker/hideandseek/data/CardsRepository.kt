package ro.go.stecker.hideandseek.data

import ro.go.stecker.hideandseek.R

//The probability property stored in each element is actually the sum of all the probabilities of the previous elements
//This way, the probability to pull a card in the list is actually (probability of card) - (probability of previous card) / 100
//This is used for choosing random cards with weighted probabilities (https://stackoverflow.com/questions/17250568/randomly-choosing-from-a-list-with-weighted-probabilities)

val CardsRepository: List<Card> = listOf(
//Curses
    Card(
        id = 0,
        name = R.string.curse_bad_influencer,
        type = CardType.Curse,
        image = R.drawable.curse_of_the_bad_influencer,
        probability = 1
    ),
    Card(
        id = 1,
        name = R.string.curse_bird_guide,
        type = CardType.Curse,
        image = R.drawable.curse_of_the_bird_guide,
        probability = 2
    ),
    Card(
        id = 2,
        name = R.string.curse_distant_cuisine,
        type = CardType.Curse,
        image = R.drawable.curse_of_the_distant_cuisine,
        probability = 3
    ),
    Card(
        id = 3,
        name = R.string.curse_drained_brain,
        type = CardType.Curse,
        image = R.drawable.curse_of_the_drained_brain,
        probability = 4
    ),
    Card(
        id = 4,
        name = R.string.curse_egg_partner,
        type = CardType.Curse,
        image = R.drawable.curse_of_the_egg_partner,
        probability = 5
    ),
//    TODO MODIFY IMAGE
    Card(
        id = 5,
        name = R.string.curse_endless_tumble,
        type = CardType.Curse,
        image = R.drawable.curse_of_the_endless_tumble,
        probability = 6
    ),
    Card(
        id = 6,
        name = R.string.curse_evergrowing_economy,
        type = CardType.Curse,
        image = R.drawable.curse_of_the_evergrowing_economy,
        probability = 7
    ),
    Card(
        id = 7,
        name = R.string.curse_gamblers_feet,
        type = CardType.Curse,
        image = R.drawable.curse_of_the_gamblers_feet,
        probability = 8
    ),
    Card(
        id = 8,
        name = R.string.curse_hidden_hangman,
        type = CardType.Curse,
        image = R.drawable.curse_of_the_hidden_hangman,
        probability = 9
    ),
    Card(
        id = 9,
        name = R.string.curse_impossible_riddle,
        type = CardType.Curse,
        image = R.drawable.curse_of_the_impossible_riddle,
        probability = 10
    ),
    Card(
        id = 10,
        name = R.string.curse_impressionable_consumer,
        type = CardType.Curse,
        image = R.drawable.curse_of_the_impressionable_consumer,
        probability = 11
    ),
    Card(
        id = 11,
        name = R.string.curse_impulsive_buyer,
        type = CardType.Curse,
        image = R.drawable.curse_of_the_impulsive_buyer,
        probability = 12
    ),
    Card(
        id = 12,
        name = R.string.curse_jammed_door,
        type = CardType.Curse,
        image = R.drawable.curse_of_the_jammed_door,
        probability = 13
    ),
    Card(
        id = 13,
        name = R.string.curse_luxury_car,
        type = CardType.Curse,
        image = R.drawable.curse_of_the_luxury_car,
        probability = 14
    ),
    Card(
        id = 14,
        name = R.string.curse_mediocre_travel_agent,
        type = CardType.Curse,
        image = R.drawable.curse_of_the_mediocre_travel_agent,
        probability = 15
    ),
    Card(
        id = 15,
        name = R.string.curse_music_guru,
        type = CardType.Curse,
        image = R.drawable.curse_of_the_music_guru,
        probability = 16
    ),
    Card(
        id = 16,
        name = R.string.curse_neverending_forest,
        type = CardType.Curse,
        image = R.drawable.curse_of_the_neverending_forest,
        probability = 17
    ),
    Card(
        id = 17,
        name = R.string.curse_overflowing_chalice,
        type = CardType.Curse,
        image = R.drawable.curse_of_the_overflowing_chalice,
        probability = 18
    ),
    Card(
        id = 18,
        name = R.string.curse_right_turn,
        type = CardType.Curse,
        image = R.drawable.curse_of_the_right_turn,
        probability = 19
    ),
    Card(
        id = 19,
        name = R.string.curse_spotty_memory,
        type = CardType.Curse,
        image = R.drawable.curse_of_the_spotty_memory,
        probability = 20
    ),
    Card(
        id = 20,
        name = R.string.curse_u_turn,
        type = CardType.Curse,
        image = R.drawable.curse_of_the_u_turn,
        probability = 21
    ),
    Card(
        id = 21,
        name = R.string.curse_unguided_tourist,
        type = CardType.Curse,
        image = R.drawable.curse_of_the_unguided_tourist,
        probability = 22
    ),
    Card(
        id = 22,
        name = R.string.curse_urban_explorer,
        type = CardType.Curse,
        image = R.drawable.curse_of_the_urban_explorer,
        probability = 23
    ),
    Card(
        id = 23,
        name = R.string.curse_voodoo_doll,
        type = CardType.Curse,
        image = R.drawable.curse_of_the_voodoo_doll,
        probability = 24
    ),
    Card(
        id = 24,
        name = R.string.curse_zoologist,
        type = CardType.Curse,
        image = R.drawable.curse_of_the_zoologist,
        probability = 25
    ),


    Card(
        id = 25,
        name = R.string.randomize_question,
        type = CardType.PowerUp,
        image = R.drawable.randomize_question,
        probability = 29
    ),
    Card(
        id = 26,
        name = R.string.veto_question,
        type = CardType.PowerUp,
        image = R.drawable.veto_question,
        probability = 33
    ),
    Card(
        id = 27,
        name = R.string.duplicate_card,
        type = CardType.PowerUp,
        image = R.drawable.duplicate_another_card,
        probability = 35
    ),
//    TODO MODIFY PROBABILITY
    Card(
        id = 28,
        name = R.string.move,
        type = CardType.PowerUp,
        image = R.drawable.move,
        probability = 37
    ),
    Card(
        id = 29,
        name = R.string.discard_1_draw_2,
        type = CardType.PowerUp,
        image = R.drawable.discard_1_draw_2,
        probability = 41
    ),
    Card(
        id = 30,
        name = R.string.discard_2_draw_3,
        type = CardType.PowerUp,
        image = R.drawable.discard_2_draw_3,
        probability = 45
    ),


    Card(
        id = 31,
        name = R.string.three_min_bonus,
        type = CardType.TimeBonus,
        image = R.drawable.three_minute_bonus,
        probability = 70
    ),
    Card(
        id = 32,
        name = R.string.five_min_bonus,
        type = CardType.TimeBonus,
        image = R.drawable.five_minute_bonus,
        probability = 85
    ),
    Card(
        id = 33,
        name = R.string.ten_min_bonus,
        type = CardType.TimeBonus,
        image = R.drawable.ten_minute_bonus,
        probability = 95
    ),
    Card(
        id = 34,
        name = R.string.fifteen_min_bonus,
        type = CardType.TimeBonus,
        image = R.drawable.fifteen_minute_bonus,
        probability = 98
    ),
    Card(
        id = 35,
        name = R.string.twenty_min_bonus,
        type = CardType.TimeBonus,
        image = R.drawable.twenty_minute_bonus,
        probability = 100
    ),

)