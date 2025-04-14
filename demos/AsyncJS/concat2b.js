const fs = require('fs');

function readFileAsync(path, encoding) {
  return new Promise(function (resolve, reject) {
    fs.readFile(path, encoding, function (err, data) {
      if (err) reject(err);
      else resolve(data);
    });
  });
}

function writeFileAsync(path, content) {
  return new Promise(function (resolve, reject) {
    fs.writeFile(path, content, function (err) {
      if (err) reject(err);
      else resolve();
    });
  });
}

const args = process.argv.slice(2);

if (args.length !== 3) {
  console.log('Usage: node concat2b.js <inputFile1> <inputFile2> <outputFile>');
  process.exit(1);
}

const [inputFile1, inputFile2, outputFile] = args;

readFileAsync(inputFile1, 'utf8')
  .then(data1 =>
    readFileAsync(inputFile2, 'utf8')
      .then(data2 => data1 + '\n' + data2)
  )
  .then(combined => writeFileAsync(outputFile, combined))
  .then(() => console.log(`File ${outputFile} written successfully.`))
  .catch(err => console.error('Error:', err.message));
