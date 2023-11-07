# Make Space Code Challenge

This is a sample project based on this code challenge

https://www.geektrust.com/coding/detailed/make-space

## Project requirements

Make Space Ltd. is a startup offering a co-working space to individuals, freelancers and startups. They provide a common
workspace where anyone can come and work. Along with it, they have dedicated meeting rooms that their customers can book
for private discussions.

Rules

    1. Bookings can be made only in a single day from 00:00 to night 23:45. It cannot overlap across days. So you cannot book from 23:00 to 01:00, but can from 23:00 to 23:45.
    2. A booking can be started and ended only in 15 minute intervals, i.e. XX:00, XX:15, XX:30, XX:45. This means a booking can be made at 01:15 or 16:00 but not 15:35 or 16:03.
    3. The rooms will be allocated only to those who book them, on a first come first serve basis.
    4. The most optimal room which can accommodate the number of people will be allocated. For eg., if you asked for a 4 person capacity requirement then the D-Tower (7 person capacity) will be allocated, provided it is available.
    5. In case if the room of desired capacity is not available, the next available capacity room will be allocated. For eg., If you asked for the 4 person capacity room between 12:00 to 13:00 and the D-Tower is not available then the G-Mansion will be allocated, provided it is available.
    6. No meetings can be scheduled during the buffer time. If the booking time overlaps with the buffer time NO_VACANT_ROOM should be printed.
    7. Bookings can be only made for 2 or more people and upto a maximum of 20 people. If the person capacity for booking is outside of 2-20 range NO_VACANT_ROOM should be printed.
    8. Time input should follow HH:MM format (24 hours format).

Input Constraints

    1. Time will be in HH:MM (24 hours) format.
    2. Time input should always consider the 15 minute time interval
    3. For all the time inputs end_time > start_time