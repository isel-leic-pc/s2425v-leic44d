const fs = require('fs');

const args = process.argv.slice(2);

if (args.length !== 3) {
  console.log('Usage: node concat1.js <inputFile1> <inputFile2> <outputFile>');
  process.exit(1);
}

const [inputFile1, inputFile2, outputFile] = args;

fs.readFile(inputFile1, 'utf8', function (err, data1) {
  if (err) {
    console.error(`Error reading ${inputFile1}:`, err.message);
    return;
  }

  fs.readFile(inputFile2, 'utf8', function (err, data2) {
    if (err) {
      console.error(`Error reading ${inputFile2}:`, err.message);
      return;
    }

    const combined = data1 + '\n' + data2;

    fs.writeFile(outputFile, combined, function (err) {
      if (err) {
        console.error(`Error writing to ${outputFile}:`, err.message);
        return;
      }

      console.log(`File ${outputFile} written successfully.`);
    });
  });
});
