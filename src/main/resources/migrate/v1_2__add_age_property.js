//mongeez formatted javascript
//changeset system:v1_2
db.person.insert({
    "_id": 2,
    "name": "thomas"
});

db.person.update(
    {}, // no search query
    {
        $set: {
            age: 12
        }
    },
    { multi: true }
);
