package fr.kevtc.luckyores;

import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Dye;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import java.util.*;

public class OresBreak implements Listener {

    private final ArrayList<Material> ores;
    private final LuckyOres plugin;

    public OresBreak(LuckyOres lo){
        ores = new ArrayList<>();
        plugin = lo;
        Set<String> configOres = plugin.getConfig().getKeys(false);
        for (String currentOre : configOres){
            ores.add(Material.matchMaterial(currentOre));
        }
    }

    @EventHandler
    public void oresBreakEvent(BlockBreakEvent e){
        Material block = e.getBlock().getType();
        if (ores.contains(block)){
            ArrayList<Object> drops = new ArrayList<>();
            e.setCancelled(true);
            if (plugin.getConfig().getConfigurationSection(""+block).contains("items")){
                ItemStack drop = genItem(""+block+".items");
                e.getBlock().setType(Material.AIR);
                drops.add(drop);
            }

            if (plugin.getConfig().getConfigurationSection(""+block).contains("effects")){
                for(PotionEffect oldeffect : e.getPlayer().getActivePotionEffects())
                {
                    e.getPlayer().removePotionEffect(oldeffect.getType());
                }

                PotionEffect effect = genEffect(""+block+".effects");
                e.getBlock().setType(Material.AIR);
                drops.add(effect);
            }

            if (plugin.getConfig().getConfigurationSection(""+block).contains("xp")){
                e.getBlock().setType(Material.AIR);
                int xp = plugin.getConfig().getInt(""+block+".xp");
                if (xp < 0){
                    xp = new Random().nextInt(3)+1;
                }
                drops.add(xp);
            }

            if (plugin.getConfig().getConfigurationSection(""+block).contains("potions")){
                e.getBlock().setType(Material.AIR);
                Potion p = genPotion(""+block+".potions");
                drops.add(p.toItemStack(1));
            }
            if (plugin.getConfig().getConfigurationSection(""+block).contains("entity")){
                e.getBlock().setType(Material.AIR);
                EntityType ent = genEntity(""+block+".entity");
                drops.add(ent);
            }
            randDrop(drops, e);
        }
    }

    public void randDrop(ArrayList<Object> drops, BlockBreakEvent e){
        if (drops.size() != 0){
            int indx = 0;
            if (drops.size() > 1){
                indx = new Random().nextInt(drops.size());
                while (drops.get(indx) == null){
                    indx = new Random().nextInt(drops.size());
                }
            }
            if (drops.get(indx) instanceof PotionEffect){
                PotionEffect drop = (PotionEffect) drops.get(indx);
                e.getPlayer().addPotionEffect(drop);
            } else if (drops.get(indx) instanceof Integer) {
                int lvl = (Integer) drops.get(indx);
                Dye lapis = new Dye();
                lapis.setColor(DyeColor.BLUE);
                e.getPlayer().getInventory().addItem(lapis.toItemStack(lvl));
                e.getPlayer().giveExpLevels(lvl);
            } else if (drops.get(indx) instanceof  EntityType) {
                e.getPlayer().getWorld().spawnEntity(e.getPlayer().getLocation(), (EntityType) drops.get(indx));
            } else if (drops.get(indx) != null){
                ItemStack drop = (ItemStack) drops.get(indx);
                e.getBlock().getWorld().dropItem(e.getBlock().getLocation(), drop);
            }
        }
    }

    public ItemStack genItem(String path){
        HashMap<ItemStack, Integer> drops = new HashMap<>();
        List<String> listDrops = plugin.getConfig().getStringList(path);
        for (String currentDrop : listDrops){
            String[] item = currentDrop.split(",");
            if (item.length == 3){
                drops.put(new ItemStack(Material.matchMaterial(item[0]), Integer.parseInt(item[1])), Integer.parseInt(item[2]));
            } else {
                int chance = Integer.parseInt(item[2]);
                ItemStack itemspe = new ItemStack(Material.matchMaterial(item[0]), Integer.parseInt(item[1]));
                short dur = itemspe.getType().getMaxDurability();
                itemspe.setDurability((short) (dur - (Short.parseShort(item[3]))));
                drops.put(itemspe, chance);
            }
        }
        return getRandomItem(drops);
    }

    public PotionEffect genEffect(String path){
        List<String> listEffects = plugin.getConfig().getStringList(path);
        PotionEffect[] effects = new PotionEffect[listEffects.size()];
        int i = 0;
        for (String currentEffect : listEffects){
            String[] effect = currentEffect.split(",");
            effects[i] = new PotionEffect(PotionEffectType.getByName(effect[0]), Integer.parseInt(effect[1])*20, Integer.parseInt(effect[2])-1);
            i++;
        }
        return getRandomEffect(effects);
    }

    public EntityType genEntity(String path){
        List<String> listEntity = plugin.getConfig().getStringList(path);
        HashMap<EntityType, Integer> entities = new HashMap<>();
        for (String currentEntitie : listEntity){
            String[] entity = currentEntitie.split(",");
            entities.put(EntityType.valueOf(entity[0]), Integer.parseInt(entity[1]));
        }
        return genRandomEntity(entities);
    }

    private EntityType genRandomEntity(HashMap<EntityType, Integer> entities){
        EntityType entity = null;
        Set<EntityType> keys = entities.keySet();
        double rand = Math.random()*100;
        for (EntityType key : keys){
            if (rand <= entities.get(key)){
                entity = key;
            }
        }
        return entity;
    }

    public Potion genPotion(String path){
        List<String> listPotions = plugin.getConfig().getStringList(path);
        Potion[] potions = new Potion[listPotions.size()];
        int i = 0;
        for (String currentPotion : listPotions){
            String[] potion = currentPotion.split(",");
            Potion p = new Potion(PotionType.valueOf(potion[0]),Integer.parseInt(potion[1]));
            if (Boolean.parseBoolean(potion[2])){
                p.extend();
            }
            if (Boolean.parseBoolean(potion[3])){
                p.splash();
            }
            potions[i] = p;
            i++;
        }
        return genRandomPotion(potions);
    }

    private ItemStack getRandomItem(HashMap<ItemStack, Integer> map){
        int size = map.size();
        ItemStack[] items = new ItemStack[size];
        int[] chance = new int[size];

        Set<ItemStack> keys = map.keySet();
        int indx = 0;
        for (ItemStack key : keys){
            items[indx] = key;
            chance[indx] = map.get(key);
            indx++;
        }

        double rand = Math.random()*100;
        int sum = 0;
        for (int i = 0; i < size; i++){
            sum+=chance[i];
            if (rand <= sum){
                return items[i];
            }
        }

        return null;
    }

    private PotionEffect getRandomEffect(PotionEffect[] effects) {
        int size = effects.length;
        int rand = new Random().nextInt(size);
        return effects[rand];
    }

    private Potion genRandomPotion(Potion[] potions){
        int size = potions.length;
        int rand = new Random().nextInt(size);
        return potions[rand];
    }

}
