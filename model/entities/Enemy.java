package code.model.entities;
import code.world.Room;

public abstract class Enemy extends Entity {
    private static final long serialVersionUID = 1L;

    protected int damage;
    protected int expReward;
    protected int immobilizedTurns = 0; 

    public Enemy(String name, int startX, int startY, int maxHp, int baseDamage, int dungeonLevel, int expReward) {
        super(name, startX, startY, maxHp);
        this.damage = (int)(baseDamage * (1.0 + 0.2 * (dungeonLevel - 1)));
        this.expReward = expReward;
    }
    
    public Enemy(String name, int x, int y, int maxHp, int damage) {
        super(name, x, y, maxHp);
        this.damage = damage; 
        this.expReward = 50; 
    }

    public int getDamage() { return damage; }
    public int getExpReward() { return expReward; } 
    
    public void immobilize(int turns) {
        this.immobilizedTurns = Math.max(this.immobilizedTurns, turns);
    }

    public void onDeath(Player killer, Room room) {
        killer.gainExp(this.expReward);
    }

    public void takeTurn(Player player, Room room) {
        if (!this.isAlive()) return;
        
        int manhattanDistance = Math.abs(player.getX() - this.getX()) + Math.abs(player.getY() - this.getY());

        if (manhattanDistance == 1) {
            this.attack(player, this.damage); 
            return; 
        }

        if (this.immobilizedTurns > 0) {
            this.immobilizedTurns--;
            return; 
        }

        double distance = Math.sqrt(Math.pow(player.getX() - this.getX(), 2) + Math.pow(player.getY() - this.getY(), 2));
        int targetX = this.getX(); 
        int targetY = this.getY();
        
        int[] dx = {-1, 1, 0, 0};
        int[] dy = {0, 0, -1, 1};

        if (distance <= 4.0) {
            double minDistance = Double.MAX_VALUE;
            
            for (int i = 0; i < 4; i++) {
                int tx = this.getX() + dx[i];
                int ty = this.getY() + dy[i];

                if (isValidMove(tx, ty, room)) {
                    double newDist = Math.sqrt(Math.pow(player.getX() - tx, 2) + Math.pow(player.getY() - ty, 2));
                    if (newDist < minDistance) {
                        minDistance = newDist;
                        targetX = tx; 
                        targetY = ty;
                    }
                }
            }
        } else {
            java.util.Random rand = new java.util.Random();
            int dir = rand.nextInt(4);
            int tx = this.getX() + dx[dir];
            int ty = this.getY() + dy[dir];
            
            if (isValidMove(tx, ty, room)) {
                targetX = tx;
                targetY = ty;
            }
        }

        if (targetX != this.getX() || targetY != this.getY()) {
            this.setPosition(targetX, targetY);
        }
    }

    protected boolean isValidMove(int tx, int ty, Room room) {
        if (tx < 0 || tx >= room.getWidth() || ty < 0 || ty >= room.getHeight()) return false;
        
        code.world.Tile targetTile = room.getTile(tx, ty);
        if (targetTile == null || !targetTile.isWalkable()) return false;
        
        if (room.getEnemyAt(tx, ty) != null) return false;
        
        if (targetTile instanceof code.world.tiles.LavaTile) return false;
        if (targetTile instanceof code.world.tiles.AbyssTile) return false;

        code.model.interfaces.Interactable content = targetTile.getContent();
        if (content instanceof code.model.objects.Trap) return false;
        if (content instanceof code.model.objects.Chest) return false;
        if (content instanceof code.model.objects.UpgradeStation && !((code.model.objects.UpgradeStation)content).isUsed()) return false;
        
        return true;
    }
}