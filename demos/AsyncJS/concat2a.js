const fs = require('fs').promises;

const args = process.argv.slice(2);

if (args.length !== 3) {
  console.log('Usage: node concat2a.js <inputFile1> <inputFile2> <outputFile>');
  process.exit(1);
}

const [inputFile1, inputFile2, outputFile] = args;

fs
  .readFile(inputFile1, 'utf8')
  .then(data1 =>
    fs
      .readFile(inputFile2, 'utf8')
      .then(data2 => data1 + '\n' + data2)
  )
  .then(combined => fs.writeFile(outputFile, combined))
  .then(() => console.log(`File ${outputFile} written successfully.`))
  .catch(err => console.error('Error:', err.message));
