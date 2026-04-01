# Deltarune 2D Dungeon

![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Swing](https://img.shields.io/badge/Java_Swing-007396?style=for-the-badge&logo=java&logoColor=white)

A 2D roguelike game engine inspired by the "Mystery Dungeon" series, built entirely from scratch in pure Java (Swing/AWT). No external game engines or third-party libraries were used. 

This project was developed as a rigorous implementation of Object-Oriented Programming (OOP) concepts, applying design patterns, S.O.L.I.D. principles, and a Data-Driven architecture.

<div align="center">
  <img src="code/assets/introduc.gif" alt="Deltarune 2D Dungeon Gameplay" width="600"/>
</div>

## Core Features

* **Data-Driven Procedural Dungeons:** The engine dynamically reads `.txt` files from an assets folder to build rooms. You can add new level layouts without touching a single line of Java code.
* **Turn-Based Combat:** Grid-based tactical action. Enemies have their own basic AI to track and attack the player.
* **Inventory System:** Uses Java Generics for type-safe management of items (weapons, potions) within a limited capacity.
* **Multithreading:** A background thread (`HungerThread`) continuously drains the player's hunger over time without freezing the main rendering loop.
* **Data Persistence:** Save and load functionality implemented via binary object serialization (`java.io`).

## OOP Architecture & Design

The codebase is structured for maintainability and scalability, strictly following academic software engineering requirements:

* **Design Patterns:** Uses the Strategy pattern (for the upgrade station system) and an MVC (Model-View-Controller) architecture.
* **The 4 Pillars of Polymorphism:**
  * *Subtyping (Inclusion):* A generic `Interactable` interface handles traps, chests, and level exits.
  * *Parametric:* Compile-time safe generic lists for the inventory.
  * *Overloading:* Context-dependent attack methods (e.g., attacking an enemy vs. interacting with a tile).
  * *Coercion:* Primitive type promotion for damage calculations and object downcasting.
* **Exception Handling:** A robust hierarchy separating Checked exceptions (e.g., `InventoryFullException`) from Unchecked exceptions (e.g., `IllegalMoveException`).

## Controls

| Key | Action |
| :---: | :--- |
| **W, A, S, D** or **Arrows** | Move across the grid |
| **Space** | Attack / Interact with the environment |
| **1, 2, 3...** | Use items from the inventory |
| **G** | Save game (only on safe tiles) |
| **ESC** | Save game & Go to Menu |
