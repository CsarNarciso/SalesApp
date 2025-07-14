FROM node
WORKDIR /app
 
# Copy json packages (files) for dependencie references!
COPY package*.json ./
 
# And install dependencies
RUN npm install
 
COPY . .
CMD ["npm", "run", "dev", "--", "--host"]
