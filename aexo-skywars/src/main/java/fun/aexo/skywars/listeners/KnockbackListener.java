package fun.aexo.skywars.listeners;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.projectile.EntityProjectile;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.entity.EntityMotionEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.level.Position;
import cn.nukkit.math.Vector3;

public class KnockbackListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        EntityDamageEvent.DamageCause cause = event.getCause();

        if (cause == EntityDamageEvent.DamageCause.ENTITY_ATTACK || cause == EntityDamageEvent.DamageCause.PROJECTILE) {
            
            // Ignore projectile damager check if intended, or adjust projectile logic
            if (event.getDamager() instanceof EntityProjectile) {
                return;
            }

            // Cancel Nukkit's default knockback handling
            event.setKnockBack(0f);

            float knockback = 0.3f;
            float height = 0.3f;

            if (event.getDamager() instanceof Player player) {
                Item item = player.getInventory().getItemInHand();

                // Check if the item has any enchantments
                if (item.hasEnchantments()) {
                    Enchantment knockBackEnchantment = item.getEnchantment(Enchantment.ID_KNOCKBACK);
                    if (knockBackEnchantment != null) {
                        knockback += knockBackEnchantment.getLevel() * 0.1f;
                        height += knockBackEnchantment.getLevel() * 0.02f;
                    }
                }
            }

            // Apply custom calculated knockback
            knockBack(event.getEntity(), event.getDamager(), knockback, height * 1.35d);
        }
    }

    @EventHandler
    public void onEntityMotion(EntityMotionEvent event) {
        // Prevent flat ground motion reset glitches if Y delta is 0
        if (event.getMotion().y == 0) {
            event.setCancelled(true);
        }
    }

    public static void knockBack(Entity self, Position attacker, double base, double height) {
        double x = self.x - attacker.x;
        double z = self.z - attacker.z;
        double f = Math.sqrt(x * x + z * z);

        if (f <= 0) {
            f = 1;
        }

        f = 1 / f;

        Vector3 motion = new Vector3(self.motionX, self.motionY, self.motionZ);
        motion.x /= 2.0d;
        motion.z /= 2.0d;

        motion.x += x * f * base * 1.33d;
        motion.y = height;
        motion.z += z * f * base * 1.33d;

        self.setMotion(motion);
    }
}
