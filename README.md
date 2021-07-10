# LuckyOres
 Minecraft plugin LuckyOres. 
 This plugin allows you to customize drops of blocks with items, potions, effects and entities.
 
 ---

 Examples:
 
 ```java
#items: TYPE,(int)QUANTITY,(int) %CHANCE(total ~= 100), [(short)DURABILITY -> optional]
#potions: TYPE,(seconds)DURATION,(int)AMPLIFIER
#xp: (int) level to give and amount of lapis gived (-1 -> random between 1 and 3)
#potions: TYPE, (int) level, (boolean) extend, (boolean) splash
#entity: TYPE, (int) %CHANCE

DIAMOND_ORE:
  items:
    - DIAMOND_CHESTPLATE,1,20
    - DIAMOND_PICKAXE,1,10,50
LAPIS_ORE:
  effects:
    - BLINDNESS,5,1
    - FAST_DIGGING,10,1
    - HEAL,3,1
    - POISON,5,1
EMERALD_ORE:
  xp: -1 
GOLD_ORE:
  potions:
    - INSTANT_HEAL,1,false,true
    - INSTANT_DAMAGE,1,false,true
  entity:
    - CREEPER, 20
