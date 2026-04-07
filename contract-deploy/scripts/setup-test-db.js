const mysql = require('mysql');

const connection = mysql.createConnection({
  host: 'localhost',
  user: 'root',
  password: 'root',
});

connection.connect((err) => {
  if (err) {
    console.error('Error connecting to MySQL:', err);
    process.exit(1);
  }

  // Drop existing database if it exists
  connection.query('DROP DATABASE IF EXISTS rwa_contract_test', (err) => {
    if (err) {
      console.error('Error dropping database:', err);
      process.exit(1);
    }

    // Create fresh database
    connection.query('CREATE DATABASE rwa_contract_test', (err) => {
      if (err) {
        console.error('Error creating database:', err);
        process.exit(1);
      }
      console.log('Test database created successfully');
      connection.end();
    });
  });
});

