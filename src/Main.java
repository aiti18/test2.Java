package kg.geeks.game.logic;

import java.util.Random;

// Базовый класс Hero
abstract class Hero {
    private String name;
    private int health;
    private int damage;
    private SuperAbility ability;

    public Hero(String name, int health, int damage, SuperAbility ability) {
        this.name = name;
        this.health = health;
        this.damage = damage;
        this.ability = ability;
    }

    public abstract void applySuperPower(Boss boss, Hero[] heroes);

    public String getName() {
        return name;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public int getDamage() {
        return damage;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

    public SuperAbility getAbility() {
        return ability;
    }
}

// Enum для суперспособностей
enum SuperAbility {
    BOOST, INVISIBLE, SUMMON, PRETEND_DEAD;
}

// Класс Boss
class Boss {
    private int health;
    private int damage;
    private int initialHealth;

    public Boss(int health, int damage) {
        this.health = health;
        this.damage = damage;
        this.initialHealth = health;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public int getDamage() {
        return damage;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

    public int getInitialHealth() {
        return initialHealth;
    }
}

// Герой Magic, увеличивающий атаку союзников
class Magic extends Hero {
    private int attackBoost;

    public Magic(String name, int health, int damage, int attackBoost) {
        super(name, health, damage, SuperAbility.BOOST);
        this.attackBoost = attackBoost;
    }

    @Override
    public void applySuperPower(Boss boss, Hero[] heroes) {
        for (Hero hero : heroes) {
            if (hero.getHealth() > 0 && hero != this) {
                hero.setDamage(hero.getDamage() + attackBoost);
            }
        }
        System.out.println(this.getName() + " увеличил атаку всех героев на " + attackBoost);
    }
}

// Герой Avrora, входящий в невидимость
class Avrora extends Hero {
    private boolean isInvisible = false;
    private int invisibleRounds = 0;
    private int storedDamage = 0;
    private boolean usedInvisibility = false;

    public Avrora(String name, int health, int damage) {
        super(name, health, damage, SuperAbility.INVISIBLE);
    }

    @Override
    public void applySuperPower(Boss boss, Hero[] heroes) {
        if (!usedInvisibility) {
            isInvisible = true;
            invisibleRounds = 2;
            usedInvisibility = true;
            System.out.println(this.getName() + " вошла в режим невидимости!");
        }

        if (isInvisible) {
            System.out.println(this.getName() + " невидима и не получает урона.");
            storedDamage += boss.getDamage();
            invisibleRounds--;
            if (invisibleRounds == 0) {
                isInvisible = false;
                boss.setHealth(boss.getHealth() - storedDamage);
                System.out.println(this.getName() + " вышла из невидимости и вернула " + storedDamage + " урона боссу.");
                storedDamage = 0;
            }
        }
    }

    @Override
    public void setHealth(int health) {
        if (!isInvisible) {
            super.setHealth(health);
        } else {
            System.out.println(this.getName() + " не получила урона, так как она невидима.");
        }
    }
}

// Герой Druid, призывающий ангела или ворона
class Druid extends Hero {
    private boolean usedSummon = false;
    private Random random = new Random();

    public Druid(String name, int health, int damage) {
        super(name, health, damage, SuperAbility.SUMMON);
    }

    @Override
    public void applySuperPower(Boss boss, Hero[] heroes) {
        if (!usedSummon) {
            usedSummon = true;
            boolean summonAngel = random.nextBoolean();

            if (summonAngel) {
                for (Hero hero : heroes) {
                    if (hero instanceof Medic) {
                        ((Medic) hero).setHealPoints(((Medic) hero).getHealPoints() + 20);
                        System.out.println("Друид призвал ангела! Увеличена сила лечения медика.");
                    }
                }
            } else {
                if (boss.getHealth() < boss.getInitialHealth() / 2) {
                    boss.setDamage(boss.getDamage() * 3 / 2);
                    System.out.println("Друид призвал ворона! Босс стал более агрессивным.");
                }
            }
        }
    }
}

// Герой Medic (добавляем класс Medic)
class Medic extends Hero {
    private int healPoints;

    public Medic(String name, int health, int damage, int healPoints) {
        super(name, health, damage, SuperAbility.BOOST);
        this.healPoints = healPoints;
    }

    public int getHealPoints() {
        return healPoints;
    }

    public void setHealPoints(int healPoints) {
        this.healPoints = healPoints;
    }

    @Override
    public void applySuperPower(Boss boss, Hero[] heroes) {
        // Лечит союзников
        for (Hero hero : heroes) {
            if (hero != this && hero.getHealth() > 0) {
                hero.setHealth(hero.getHealth() + healPoints);
                System.out.println(this.getName() + " лечит " + hero.getName() + " на " + healPoints + " здоровья.");
            }
        }
    }
}

// Герой TrickyBastard, притворяющийся мертвым
class TrickyBastard extends Hero {
    private boolean isPretendingDead = false;
    private Random random = new Random();

    public TrickyBastard(String name, int health, int damage) {
        super(name, health, damage, SuperAbility.PRETEND_DEAD);
    }

    @Override
    public void applySuperPower(Boss boss, Hero[] heroes) {
        if (!isPretendingDead && random.nextBoolean()) {
            isPretendingDead = true;
            System.out.println(this.getName() + " притворился мертвым и не получил урон!");
        } else if (isPretendingDead) {
            isPretendingDead = false;
            System.out.println(this.getName() + " вернулся в бой.");
        }
    }

    @Override
    public void setHealth(int health) {
        if (isPretendingDead) {
            System.out.println(this.getName() + " не получил урон, так как притворился мертвым.");
        } else {
            super.setHealth(health);
        }
    }
}

// Главный класс для запуска игры
public class RPG_Game {
    public static void startGame() {
        Boss boss = new Boss(1000, 50);
        Hero[] heroes = {
                new Magic("Magic", 300, 20, 5),
                new Avrora("Avrora", 250, 15),
                new Druid("Druid", 270, 10),
                new Medic("Medic", 200, 10, 30), // Добавляем медика
                new TrickyBastard("TrickyBastard", 200, 30)
        };

        for (Hero hero : heroes) {
            hero.applySuperPower(boss, heroes);
        }
    }

    public static void main(String[] args) {
        startGame();
    }
}
