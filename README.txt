Directions for ThreadedPhysics engine:

When the first frame opens the user is prompted to choose either "Start Pure Physics" or "Pendulums". If 
"Start Pure Physics" is chosen, a new frame is opened with a control frame, responsible for adding new wheels, adding 
random  velocity, and sending back to main menu. If "Pendulums" are chosen a new frame will open with a control frame 
responsible for adding new pendulums, and sending back to main menu.

Known issues:

1: Pendulum collision is not implemented yet.
2: Collision detection is n^2, quad trees should be implemented.
3: Reuseability is low, objects depend on variables outside of class.
4: Save method works, but no load method has been created.
5: GUI for creating a specific pendulum is not finished.